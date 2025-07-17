package tungp.android.bazarbooks

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tungp.android.bazarbooks.routes.bookRoutes
import tungp.android.bazarbooks.routes.cartRoutes
import tungp.android.bazarbooks.routes.categoryRoutes
import tungp.android.bazarbooks.routes.homeFeedsRoutes
import tungp.android.bazarbooks.routes.orderRoutes
import tungp.android.bazarbooks.services.*

fun Application.configureRouting() {
    val bookService = BookService()
    val cartService = CartService()
    val orderService = OrderService(cartService)
    val homeFeedsService = HomeFeedsService()
    val categoryService = CategoryService()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        bookRoutes(bookService)
        cartRoutes(cartService)
        orderRoutes(orderService)
        homeFeedsRoutes(homeFeedsService)
        categoryRoutes()
    }
}
