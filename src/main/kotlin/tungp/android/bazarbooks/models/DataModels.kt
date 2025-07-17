package tungp.android.bazarbooks.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Author(
    val id: Int? = null,
    val name: String,
    val biography: String? = null,
    val profileImageUrl: String? = null,
    val nationality: String? = null,
    val birthDate: String? = null,
    val isVerified: Boolean = false,
)

@Serializable
data class Vendor(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val websiteUrl: String? = null,
    val rating: Float = 0.0f,
    val totalReviews: Float = 0.0f,
    val isVerified: Boolean = false,
    val isActive: Boolean = false,
)

@Serializable
data class SpecialOffer(
    val id: Int? = null,
    val title: String,
    val description: String,
    val discountPercentage: Float,
    val discountAmount: Double? = null,
    val maxUsageCount: Int? = null,
    val currentUsageCount: Int = 0,
)

@Serializable
data class HomeFeeds(
    val bestOffers: List<SpecialOffer> = emptyList(),
    val topOfWeeks: List<Book> = emptyList(),
    val bestVendors: List<Vendor> = emptyList(),
    val bestAuthors: List<Author> = emptyList(),
)

@Serializable
data class Address(
    val id: Int,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String
)

@Serializable
data class CartItem(
    val id: Int? = null,
    val userId: Int,
    val bookId: Int,
    val quantity: Int,
    val price: Double,
    val addedAt: String = LocalDateTime.now().toString()
)

@Serializable
data class Order(
    val id: Int? = null,
    val userId: Int,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: String,
    val paymentMethod: String,
    val orderDate: String = LocalDateTime.now().toString(),
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class OrderItem(
    val id: Int? = null,
    val orderId: Int,
    val bookId: Int,
    val quantity: Int,
    val price: Double,
    val bookTitle: String
)

@Serializable
enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, COMPLETED
}

@Serializable
data class Payment(
    val id: Int? = null,
    val orderId: Int,
    val amount: Double,
    val paymentMethod: String,
    val status: PaymentStatus,
    val transactionId: String,
    val paymentDate: String = LocalDateTime.now().toString()
)

@Serializable
enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

@Serializable
data class Category(
    val id: Int? = null,
    val name: String,
    val description: String?
)

@Serializable
data class Book(
    val id: Int? = null,
    val title: String,
    val author: String,
    val isbn: String,
    val price: Double,
    val stock: Int,
    val description: String?,
    val categoryId: Int,
    val authorId: Int,
    val createdAt: String? = null
)

@Serializable
data class Wishlist(
    val id: Int? = null,
    val userId: Int,
    val bookId: Int,
    val addedAt: String = LocalDateTime.now().toString()
)

// Request DTOs
@Serializable
data class AddToCartRequest(val bookId: Int, val quantity: Int)

@Serializable
data class UpdateCartRequest(val quantity: Int)

@Serializable
data class CreateOrderRequest(
    val shippingAddress: String,
    val paymentMethod: String
)

@Serializable
data class PaymentRequest(
    val orderId: Int,
    val paymentMethod: String,
    val cardNumber: String? = null,
    val expiryDate: String? = null,
    val cvv: String? = null
)

@Serializable
data class CategoryRequest(val name: String, val description: String)

// Response DTOs
@Serializable
data class CartSummary(
    val items: List<CartItem>,
    val totalItems: Int,
    val totalAmount: Double
)

@Serializable
data class OrderResponse(
    val order: Order,
    val message: String = "Order created successfully"
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String,
    val errors: List<String> = emptyList()
)