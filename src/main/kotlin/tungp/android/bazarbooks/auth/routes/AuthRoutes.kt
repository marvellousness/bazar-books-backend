package tungp.android.bazarbooks.auth.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import tungp.android.bazarbooks.auth.data.LoginRequest
import tungp.android.bazarbooks.auth.data.RegisterRequest
import tungp.android.bazarbooks.auth.service.AuthService
import tungp.android.bazarbooks.models.ApiResponse

fun Route.authRouting(authService: AuthService) {
    post("/register") {
        val request = call.receive<RegisterRequest>()
        val response = authService.registerUser(request)

        if (response.success) {
            call.respond(HttpStatusCode.Created, response)
        } else {
            call.respond(HttpStatusCode.Conflict, response)
        }
    }

    post("/login") {
        val request = call.receive<LoginRequest>()
        val response = authService.loginUser(request)

        if (response.success) {
            call.respond(HttpStatusCode.OK, response)
        } else {
            call.respond(HttpStatusCode.Unauthorized, response)
        }
    }

    authenticate("auth-jwt") {
        get("/users") {
            val response = authService.getAllUsers()
            call.respond(HttpStatusCode.OK, response)
        }

        post("/logout") {
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}