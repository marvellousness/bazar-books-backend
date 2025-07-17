package tungp.android.bazarbooks.services

import tungp.android.bazarbooks.models.Order
import tungp.android.bazarbooks.models.OrderItem
import tungp.android.bazarbooks.models.OrderStatus
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.database.Books
import tungp.android.bazarbooks.database.OrderItems
import tungp.android.bazarbooks.database.Orders
import java.math.BigDecimal

class OrderService(private val cartService: CartService) {
    fun createOrder(userId: Int, shippingAddress: String, paymentMethod: String): Order = transaction {
        val cartSummary = cartService.getCartSummary(userId)

        if (cartSummary.items.isEmpty()) {
            throw IllegalArgumentException("Cart is empty")
        }

        // Validate stock availability
        cartSummary.items.forEach { cartItem ->
            val book = Books.select { Books.id eq cartItem.bookId }.single()
            if (book[Books.stock] < cartItem.quantity) {
                throw IllegalArgumentException("Insufficient stock for book: ${book[Books.title]}")
            }
        }

        // Create order
        val orderId = Orders.insert {
            it[Orders.userId] = userId
            it[Orders.totalAmount] = BigDecimal(cartSummary.totalAmount)
            it[Orders.status] = OrderStatus.PENDING.name
            it[Orders.shippingAddress] = shippingAddress
            it[Orders.paymentMethod] = paymentMethod
        } get Orders.id

        // Create order items and update book stock
        val orderItems = cartSummary.items.map { cartItem ->
            val book = Books.select { Books.id eq cartItem.bookId }.single()

            OrderItems.insert {
                it[OrderItems.orderId] = orderId
                it[OrderItems.bookId] = cartItem.bookId
                it[OrderItems.quantity] = cartItem.quantity
                it[OrderItems.price] = BigDecimal(cartItem.price)
                it[OrderItems.bookTitle] = book[Books.title]
            }

            // Update book stock
            Books.update({ Books.id eq cartItem.bookId }) {
                it[Books.stock] = book[Books.stock] - cartItem.quantity
            }

            OrderItem(
                orderId = orderId,
                bookId = cartItem.bookId,
                quantity = cartItem.quantity,
                price = cartItem.price,
                bookTitle = book[Books.title]
            )
        }

        // Clear cart
        cartService.clearCart(userId)

        Order(
            id = orderId,
            userId = userId,
            totalAmount = cartSummary.totalAmount,
            status = OrderStatus.PENDING,
            shippingAddress = shippingAddress,
            paymentMethod = paymentMethod,
            items = orderItems
        )
    }

    fun getOrderById(orderId: Int, userId: Int): Order? = transaction {
        Orders.select { (Orders.id eq orderId) and (Orders.userId eq userId) }
            .singleOrNull()?.let { orderRow ->
                val items = OrderItems.select { OrderItems.orderId eq orderId }.map { itemRow ->
                    OrderItem(
                        id = itemRow[OrderItems.id],
                        orderId = itemRow[OrderItems.orderId],
                        bookId = itemRow[OrderItems.bookId],
                        quantity = itemRow[OrderItems.quantity],
                        price = itemRow[OrderItems.price].toDouble(),
                        bookTitle = itemRow[OrderItems.bookTitle]
                    )
                }

                Order(
                    id = orderRow[Orders.id],
                    userId = orderRow[Orders.userId],
                    totalAmount = orderRow[Orders.totalAmount].toDouble(),
                    status = OrderStatus.valueOf(orderRow[Orders.status]),
                    shippingAddress = orderRow[Orders.shippingAddress],
                    paymentMethod = orderRow[Orders.paymentMethod],
                    orderDate = orderRow[Orders.orderDate].toString(),
                    items = items
                )
            }
    }

    fun getUserOrders(userId: Int): List<Order> = transaction {
        Orders.select { Orders.userId eq userId }
            .orderBy(Orders.orderDate to SortOrder.DESC)
            .map { orderRow ->
                val items = OrderItems.select { OrderItems.orderId eq orderRow[Orders.id] }.map { itemRow ->
                    OrderItem(
                        id = itemRow[OrderItems.id],
                        orderId = itemRow[OrderItems.orderId],
                        bookId = itemRow[OrderItems.bookId],
                        quantity = itemRow[OrderItems.quantity],
                        price = itemRow[OrderItems.price].toDouble(),
                        bookTitle = itemRow[OrderItems.bookTitle]
                    )
                }

                Order(
                    id = orderRow[Orders.id],
                    userId = orderRow[Orders.userId],
                    totalAmount = orderRow[Orders.totalAmount].toDouble(),
                    status = OrderStatus.valueOf(orderRow[Orders.status]),
                    shippingAddress = orderRow[Orders.shippingAddress],
                    paymentMethod = orderRow[Orders.paymentMethod],
                    orderDate = orderRow[Orders.orderDate].toString(),
                    items = items
                )
            }
    }

    fun updateOrderStatus(orderId: Int, newStatus: OrderStatus): Boolean = transaction {
        Orders.update({ Orders.id eq orderId }) {
            it[status] = newStatus.name
        } > 0
    }

    fun cancelOrder(orderId: Int, userId: Int): Boolean = transaction {
        val order = Orders.select { (Orders.id eq orderId) and (Orders.userId eq userId) }
            .singleOrNull() ?: return@transaction false

        if (OrderStatus.valueOf(order[Orders.status]) != OrderStatus.PENDING) {
            return@transaction false
        }

        // Restore book stock
        OrderItems.select { OrderItems.orderId eq orderId }.forEach { item ->
            val bookId = item[OrderItems.bookId]
            val orderQty = item[OrderItems.quantity]
            val currentStock = Books.select { Books.id eq bookId }.single()[Books.stock]
            Books.update({ Books.id eq bookId }) {
                it[Books.stock] = currentStock + orderQty
            }
        }

        // Update order status
        Orders.update({ Orders.id eq orderId }) {
            it[status] = OrderStatus.CANCELLED.name
        }

        true
    }
}