package tungp.android.bazarbooks.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tungp.android.bazarbooks.models.ApiResponse
import tungp.android.bazarbooks.models.CreateOrderRequest
import tungp.android.bazarbooks.models.OrderResponse
import tungp.android.bazarbooks.services.OrderService

fun Route.orderRoutes(orderService: OrderService) {
    val userId = 1
    route("/orders") {
        post {
            val request = call.receive<CreateOrderRequest>()

            try {
                val order = orderService.createOrder(userId, request.shippingAddress, request.paymentMethod)
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(true, OrderResponse(order), "Order created successfully")
                )
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(false, null, e.message ?: "Invalid request")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, null, "Error creating order", listOf(e.message ?: "Unknown error"))
                )
            }
        }

        get {
            try {
                val orders = orderService.getUserOrders(userId)
                call.respond(ApiResponse(true, orders, "Orders retrieved successfully"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, null, "Error retrieving orders", listOf(e.message ?: "Unknown error"))
                )
            }
        }

        get("/{orderId}") {
            val orderId = call.parameters["orderId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid order ID")

            try {
                val order = orderService.getOrderById(orderId, userId)
                if (order != null) {
                    call.respond(ApiResponse(true, order, "Order retrieved successfully"))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(false, null, "Order not found")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, null, "Error retrieving order", listOf(e.message ?: "Unknown error"))
                )
            }
        }

        patch("/{orderId}/cancel") {
            val orderId = call.parameters["orderId"]?.toIntOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid order ID")

            try {
                val success = orderService.cancelOrder(orderId, userId)
                if (success) {
                    call.respond(ApiResponse(true, null, "Order cancelled successfully"))
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(false, null, "Order cannot be cancelled")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(false, null, "Error cancelling order", listOf(e.message ?: "Unknown error"))
                )
            }
        }
    }
}