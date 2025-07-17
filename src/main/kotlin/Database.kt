//package tungp.android.bazarbooks
//
//import org.jetbrains.exposed.sql.Table
//import org.jetbrains.exposed.sql.javatime.date
//import java.time.LocalDate
//
//object Customers : Table() {
//    val id = integer("customer_id").autoIncrement()
//    val name = varchar("name", 255)
//    val email = varchar("email", 255).uniqueIndex()
//    val password = varchar("password", 255)
//    val address = text("address")
//    val phone = varchar("phone", 50).nullable()
//    val registrationDate = date("registration_date").default(LocalDate.now())
//    override val primaryKey = PrimaryKey(id, name = "PK_Customer_ID")
//}
//
//object Books : Table() {
//    val id = integer("book_id").autoIncrement()
//    val title = varchar("title", 255)
//    val isbn = varchar("isbn", 13).uniqueIndex()
//    val publisher = varchar("publisher", 255)
//    val publicationDate = date("publication_date").nullable()
//    val price = decimal("price", 10, 2)
//    val stockLevel = integer("stock_level")
//    val description = text("description").nullable()
//    val productForm = varchar("product_form", 3) // ONIX code, e.g., "BC" for paperback
//    val productFormDetail = varchar("product_form_detail", 4).nullable() // e.g., "B101"
//    val height = decimal("height", 10, 2).nullable() // in cm
//    val width = decimal("width", 10, 2).nullable() // in cm
//    val weight = decimal("weight", 10, 2).nullable() // in grams
//    val stock = integer("stock").default(0)
//    override val primaryKey = PrimaryKey(id, name = "PK_Book_ID")
//}
//
//object Authors : Table() {
//    val id = integer("author_id").autoIncrement()
//    val name = varchar("name", 255)
//    val biography = text("biography").nullable()
//    override val primaryKey = PrimaryKey(id, name = "PK_Author_ID")
//}
//
//object BookAuthors : Table() {
//    val bookId = integer("book_id").references(Books.id)
//    val authorId = integer("author_id").references(Authors.id)
//}
//
//object Categories : Table() {
//    val id = integer("category_id").autoIncrement()
//    val name = varchar("name", 255)
//    override val primaryKey = PrimaryKey(id, name = "PK_Category_ID")
//}
//
//object BookCategories : Table() {
//    val bookId = integer("book_id").references(Books.id)
//    val categoryId = integer("category_id").references(Categories.id)
//}
//
//object Orders : Table() {
//    val id = integer("order_id").autoIncrement()
//    val customerId = integer("customer_id").references(Customers.id)
//    val orderDate = date("order_date").default(LocalDate.now())
//    val totalAmount = decimal("total_amount", 10, 2)
//    val status = varchar("status", 50)
//    override val primaryKey = PrimaryKey(id, name = "PK_Order_ID")
//}
//
//object OrderItems : Table() {
//    val id = integer("order_item_id").autoIncrement()
//    val orderId = integer("order_id").references(Orders.id)
//    val bookId = integer("book_id").references(Books.id)
//    val quantity = integer("quantity")
//    val price = decimal("price", 10, 2)
//    override val primaryKey = PrimaryKey(id, name = "PK_OrderItem_ID")
//}
//
//object Reviews : Table() {
//    val id = integer("review_id").autoIncrement()
//    val bookId = integer("book_id").references(Books.id)
//    val customerId = integer("customer_id").references(Customers.id)
//    val rating = integer("rating")
//    val comment = text("comment").nullable()
//    val reviewDate = date("review_date").default(LocalDate.now())
//    override val primaryKey = PrimaryKey(id, name = "PK_Review_ID")
//}
//
//object Carts : Table() {
//    val id = integer("cart_id").autoIncrement()
//    val customerId = integer("customer_id").references(Customers.id)
//    override val primaryKey = PrimaryKey(id, name = "PK_Cart_ID")
//}
//
//object CartItems : Table() {
//    val id = integer("cart_item_id").autoIncrement()
//    val cartId = integer("cart_id").references(Carts.id)
//    val bookId = integer("book_id").references(Books.id)
//    val customerId = integer("customer_id").references(Customers.id)
//    val quantity = integer("quantity")
//    val price = decimal("price", 10, 2)
//    override val primaryKey = PrimaryKey(id, name = "PK_CartItem_ID")
//}
//
//object Wishlists : Table() {
//    val id = integer("wishlist_id").autoIncrement()
//    val customerId = integer("customer_id").references(Customers.id)
//    override val primaryKey = PrimaryKey(id, name = "PK_Wishlist_ID")
//}
//
//object WishlistItems : Table() {
//    val id = integer("wishlist_item_id").autoIncrement()
//    val wishlistId = integer("wishlist_id").references(Wishlists.id)
//    val bookId = integer("book_id").references(Books.id)
//    override val primaryKey = PrimaryKey(id, name = "PK_WishlistItem_ID")
//}