package tungp.android.bazarbooks

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.database.*
import tungp.android.bazarbooks.models.ApiResponse

fun main(args: Array<String>) {
    System.setProperty("io.ktor.development", "true")
    EngineMain.main(args)
}

fun Application.module() {
    initDatabase()
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    configureRouting()

    // Configure error handling
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse(false, null, cause.message ?: "Bad request")
            )
        }

        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse(false, null, "Validation failed", cause.reasons)
            )
        }

        exception<Exception> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse(false, null, "Internal server error", listOf(cause.message ?: "Unknown error"))
            )
        }
    }
}

fun Application.initDatabase() {
    Database.connect(
        "jdbc:h2:mem:bazarbooks;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
    )
    transaction {
        SchemaUtils.create(
            Users, Books, Categories, CartItems, Orders, OrderItems, Payments, Wishlists,
            Authors, Vendors, SpecialOffers // add new tables here
        )
        seedDatabase()
    }
}
