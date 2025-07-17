package tungp.android.bazarbooks.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("customer")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object Authors : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val biography = text("biography").nullable()
    val profileImageUrl = varchar("profile_image_url", 255).nullable()
    val nationality = varchar("nationality", 100).nullable()
    val birthDate = varchar("birth_date", 50).nullable()
    val isVerified = bool("is_verified").default(false)
    override val primaryKey = PrimaryKey(id)
}

object Vendors : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val logoUrl = varchar("logo_url", 255).nullable()
    val websiteUrl = varchar("website_url", 255).nullable()
    val rating = float("rating").default(0.0f)
    val totalReviews = float("total_reviews").default(0.0f)
    val isVerified = bool("is_verified").default(false)
    val isActive = bool("is_active").default(false)
    override val primaryKey = PrimaryKey(id)
}

object SpecialOffers : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    val description = text("description")
    val discountPercentage = float("discount_percentage")
    val discountAmount = double("discount_amount").nullable()
    val maxUsageCount = integer("max_usage_count").nullable()
    val currentUsageCount = integer("current_usage_count").default(0)
    override val primaryKey = PrimaryKey(id)
}

object Books : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    val author = varchar("author", 100)
    val authorId = integer("author_id").references(Authors.id)
    val isbn = varchar("isbn", 20).uniqueIndex()
    val price = decimal("price", 10, 2)
    val stock = integer("stock").default(0)
    val description = text("description").nullable()
    val categoryId = integer("category_id").references(Categories.id)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object Categories : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object CartItems : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val bookId = integer("book_id").references(Books.id)
    val quantity = integer("quantity").default(1)
    val price = decimal("price", 10, 2)
    val addedAt = datetime("added_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object Orders : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val totalAmount = decimal("total_amount", 10, 2)
    val status = varchar("status", 20).default("PENDING")
    val shippingAddress = text("shipping_address")
    val paymentMethod = varchar("payment_method", 50)
    val orderDate = datetime("order_date").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table() {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id").references(Orders.id)
    val bookId = integer("book_id").references(Books.id)
    val quantity = integer("quantity")
    val price = decimal("price", 10, 2)
    val bookTitle = varchar("book_title", 200)
    override val primaryKey = PrimaryKey(id)
}

object Payments : Table() {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id").references(Orders.id)
    val amount = decimal("amount", 10, 2)
    val paymentMethod = varchar("payment_method", 50)
    val status = varchar("status", 20).default("PENDING")
    val transactionId = varchar("transaction_id", 100)
    val paymentDate = datetime("payment_date").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}

object Wishlists : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val bookId = integer("book_id").references(Books.id)
    val addedAt = datetime("added_at").default(LocalDateTime.now())
    override val primaryKey = PrimaryKey(id)
}