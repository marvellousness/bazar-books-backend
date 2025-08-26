package tungp.android.bazarbooks.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tungp.android.bazarbooks.models.AddToCartRequest
import tungp.android.bazarbooks.models.ApiResponse
import tungp.android.bazarbooks.models.UpdateCartRequest
import tungp.android.bazarbooks.services.CartService

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Route.cartRoutes(cartService: CartService) {
    authenticate("auth-jwt") {
        route("/cart") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                try {
                    val cartSummary = cartService.getCartSummary(userId)
                    call.respond(ApiResponse(true, cartSummary, "Cart retrieved successfully"))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(false, null, "Error retrieving cart", listOf(e.message ?: "Unknown error"))
                    )
                }
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<AddToCartRequest>()

                try {
                    val cartItem = cartService.addToCart(userId, request.bookId, request.quantity)
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(true, cartItem, "Item added to cart")
                    )
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, null, e.message ?: "Invalid request")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(false, null, "Error adding item to cart", listOf(e.message ?: "Unknown error"))
                    )
                }
            }

            put("/{itemId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val itemId = call.parameters["itemId"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid item ID")

                val request = call.receive<UpdateCartRequest>()

                try {
                    val cartItem = cartService.updateCartItem(userId, itemId, request.quantity)
                    if (cartItem != null) {
                        call.respond(ApiResponse(true, cartItem, "Cart item updated"))
                    } else {
                        call.respond(ApiResponse(true, null, "Cart item removed"))
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, null, e.message ?: "Invalid request")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(false, null, "Error updating cart item", listOf(e.message ?: "Unknown error"))
                    )
                }
            }

            delete {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                try {
                    val success = cartService.clearCart(userId)
                    if (success) {
                        call.respond(ApiResponse(true, null, "Cart cleared successfully"))
                    } else {
                        call.respond(ApiResponse(false, null, "Cart was already empty"))
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse(false, null, "Error clearing cart", listOf(e.message ?: "Unknown error"))
                    )
                }
            }
        }
    }
}