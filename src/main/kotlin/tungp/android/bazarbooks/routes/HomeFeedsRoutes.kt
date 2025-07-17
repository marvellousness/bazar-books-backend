package tungp.android.bazarbooks.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tungp.android.bazarbooks.models.ApiResponse
import tungp.android.bazarbooks.services.HomeFeedsService

fun Route.homeFeedsRoutes(homeFeedsService: HomeFeedsService) {
    route("/home-feeds") {
        get {
            val feeds = homeFeedsService.getHomeFeeds()
            call.respond(ApiResponse(success = true, data = feeds, message = "Home feeds fetched successfully"))
        }
    }
}
