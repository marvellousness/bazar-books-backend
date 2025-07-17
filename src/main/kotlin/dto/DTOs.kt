package tungp.android.bazarbooks.dto

data class BookDTO(
    val id: Int? = null,
    val title: String,
    val isbn: String,
    val publisher: String,
    val publicationDate: String?,
    val price: Double,
    val stockLevel: Int,
    val description: String?,
    val productForm: String, // ONIX: e.g., "BC" for paperback
    val productFormDetail: String?, // ONIX: e.g., "B101"
    val height: Double?,
    val width: Double?,
    val weight: Double?
)

data class UserDTO(val name: String, val email: String, val password: String)
data class LoginDTO(val email: String, val password: String)
data class CartItemDTO(val bookId: Int, val quantity: Int)
data class OrderItemDTO(val bookId: Int, val quantity: Int)
data class OrderDTO(val items: List<OrderItemDTO>)