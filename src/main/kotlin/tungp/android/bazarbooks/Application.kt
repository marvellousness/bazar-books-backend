package tungp.android.bazarbooks

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.database.*
import tungp.android.bazarbooks.models.ApiResponse
import tungp.android.bazarbooks.routes.bookRoutes
import tungp.android.bazarbooks.routes.cartRoutes
import tungp.android.bazarbooks.routes.orderRoutes
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import tungp.android.bazarbooks.auth.data.TokenConfig
import tungp.android.bazarbooks.auth.routes.authRouting
import tungp.android.bazarbooks.auth.service.AuthService
import tungp.android.bazarbooks.auth.utils.TokenManager
import tungp.android.bazarbooks.routes.homeFeedsRoutes
import tungp.android.bazarbooks.services.BookService
import tungp.android.bazarbooks.services.CartService
import tungp.android.bazarbooks.services.OrderService
import tungp.android.bazarbooks.services.HomeFeedsService
import tungp.android.bazarbooks.utils.LocalDateTimeAdapter
import java.time.LocalDateTime

fun main(args: Array<String>) {
    System.setProperty("io.ktor.development", "true")
    EngineMain.main(args)
}

fun Application.module() {
    initDatabase()
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        }
    }
    install(Authentication) {
        jwt("auth-jwt") {
            val jwtIssuer = this@module.environment.config.property("jwt.issuer").getString()
            val jwtAudience = this@module.environment.config.property("jwt.audience").getString()
            val jwtSecret = this@module.environment.config.property("jwt.secret").getString()
            realm = "BazarBooks"
            verifier(JWT
                .require(Algorithm.HMAC256(jwtSecret))
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .build())
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
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

private fun Application.configureRouting() {
    // Initialize services
    val cartService = CartService()
    val orderService = OrderService(cartService)
    val bookService = BookService()
    val homeFeedsService = HomeFeedsService() // NEW

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = environment.config.property("jwt.expiresIn").getString().toLong(),
        secret = environment.config.property("jwt.secret").getString()
    )
    val tokenManager = TokenManager(tokenConfig)
    val authService = AuthService(tokenManager)

    // Configure routing
    routing {
        get("/") {
            call.respond("Bookstore API is running!")
        }

        get("/health") {
            call.respond(
                ApiResponse(
                    true,
                    mapOf("status" to "healthy", "timestamp" to System.currentTimeMillis()),
                    "Service is healthy"
                )
            )
        }

        // Add all route modules
        cartRoutes(cartService)
        orderRoutes(orderService)
        bookRoutes(bookService)
        homeFeedsRoutes(homeFeedsService) // NEW
        authRouting(authService)
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
