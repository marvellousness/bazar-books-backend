package tungp.android.bazarbooks.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tungp.android.bazarbooks.services.CategoryService

fun Route.categoryRoutes() {
    val categoryService = CategoryService()

    get("/categories") {
        call.respond(categoryService.getAllCategories())
    }
}