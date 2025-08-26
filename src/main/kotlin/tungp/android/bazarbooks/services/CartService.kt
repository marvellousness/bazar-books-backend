package tungp.android.bazarbooks.services

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.*
import tungp.android.bazarbooks.database.Books
import tungp.android.bazarbooks.database.CartItems
import tungp.android.bazarbooks.models.CartItem
import tungp.android.bazarbooks.models.CartSummary
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CartService {
    fun addToCart(userId: Int, bookId: Int, quantity: Int): CartItem? = transaction {
        // Check if a book exists and has sufficient stock
        val book = Books.select { Books.id eq bookId }.singleOrNull()
            ?: throw IllegalArgumentException("Book not found")

        val bookStock = book[Books.stock]
        if (bookStock < quantity) {
            throw IllegalArgumentException("Insufficient stock")
        }

        // Check if item already exists in cart
        val existingItem = CartItems.select {
            (CartItems.userId eq userId) and (CartItems.bookId eq bookId)
        }.singleOrNull()

        if (existingItem != null) {
            // Update existing item
            val newQuantity = existingItem[CartItems.quantity] + quantity
            if (bookStock < newQuantity) {
                throw IllegalArgumentException("Insufficient stock for total quantity")
            }

            CartItems.update({ CartItems.id eq existingItem[CartItems.id] }) {
                it[CartItems.quantity] = newQuantity
            }

            getCartItem(existingItem[CartItems.id])
        } else {
            // Add new item
            val insertedId = CartItems.insert {
                it[CartItems.userId] = userId
                it[CartItems.bookId] = bookId
                it[CartItems.quantity] = quantity
                it[CartItems.price] = book[Books.price]
            } get CartItems.id

            getCartItem(insertedId)
        }
    }

    fun updateCartItem(userId: Int, itemId: Int, quantity: Int): CartItem? = transaction {
        val cartItem = CartItems.select {
            (CartItems.id eq itemId) and (CartItems.userId eq userId)
        }.singleOrNull() ?: throw IllegalArgumentException("Cart item not found")

        if (quantity <= 0) {
            CartItems.deleteWhere { (CartItems.id eq itemId) and (CartItems.userId eq userId) }
            return@transaction null
        }

        // Check stock
        val bookStock = Books.select { Books.id eq cartItem[CartItems.bookId] }
            .single()[Books.stock]

        if (bookStock < quantity) {
            throw IllegalArgumentException("Insufficient stock")
        }

        CartItems.update({ (CartItems.id eq itemId) and (CartItems.userId eq userId) }) {
            it[CartItems.quantity] = quantity
        }

        getCartItem(itemId)
    }

    fun getCartSummary(userId: Int): CartSummary = transaction {
        val cartItems = CartItems.innerJoin(Books)
            .select { CartItems.userId eq userId }
            .map { row ->
                CartItem(
                    id = row[CartItems.id],
                    userId = row[CartItems.userId],
                    bookId = row[CartItems.bookId],
                    quantity = row[CartItems.quantity],
                    price = row[CartItems.price].toDouble(),
                    addedAt = row[CartItems.addedAt].toString()
                )
            }

        val totalItems = cartItems.size
        val totalAmount = cartItems.sumOf { it.price * it.quantity }

        CartSummary(cartItems, totalItems, totalAmount)
    }

    fun clearCart(userId: Int): Boolean = transaction {
        CartItems.deleteWhere { CartItems.userId eq userId } > 0
    }

    private fun getCartItem(itemId: Int): CartItem? = transaction {
        CartItems.select { CartItems.id eq itemId }
            .singleOrNull()?.let { row ->
                CartItem(
                    id = row[CartItems.id],
                    userId = row[CartItems.userId],
                    bookId = row[CartItems.bookId],
                    quantity = row[CartItems.quantity],
                    price = row[CartItems.price].toDouble(),
                    addedAt = row[CartItems.addedAt].toString()
                )
            }
    }
}