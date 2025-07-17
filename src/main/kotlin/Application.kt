package tungp.android.bazarbooks

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    System.setProperty("io.ktor.development", "true")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}

fun Application.initDatabase() {
    Database.connect(
    "jdbc:h2:mem:bazarbooks;DB_CLOSE_DELAY=-1",
    driver = "org.h2.Driver"
)
    transaction {
        SchemaUtils.create(
            Customers,
            Books,
            Authors,
            BookAuthors,
            Categories,
            BookCategories,
            Orders,
            OrderItems,
            Reviews,
            Carts,
            CartItems,
            Wishlists,
            WishlistItems
        )
    }
}
