package tungp.android.bazarbooks

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.dto.BookDTO
import tungp.android.bazarbooks.response.PaginatedBooksResponse
import java.math.BigDecimal

fun Application.configureRouting() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    initDatabase()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/books") {
            // Parse pagination parameters
            val page = call.parameters["page"]?.toIntOrNull() ?: 1
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 10

            // Validate pagination parameters
            if (page < 1) return@get call.respond(HttpStatusCode.BadRequest, "Page must be at least 1")
            if (limit < 1 || limit > 100) return@get call.respond(HttpStatusCode.BadRequest, "Limit must be between 1 and 100")

            // Parse filter parameters
            val title = call.parameters["title"]?.takeIf { it.isNotBlank() }
            val author = call.parameters["author"]?.takeIf { it.isNotBlank() }
            val publisher = call.parameters["publisher"]?.takeIf { it.isNotBlank() }
            val category = call.parameters["category"]?.takeIf { it.isNotBlank() }
            val minPrice = call.parameters["minPrice"]?.toDoubleOrNull()?.takeIf { it >= 0 }
            val maxPrice = call.parameters["maxPrice"]?.toDoubleOrNull()?.takeIf { it >= 0 }

            // Validate price range
            if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                return@get call.respond(HttpStatusCode.BadRequest, "minPrice cannot be greater than maxPrice")
            }

            // Calculate offset
            val offset = (page - 1) * limit

            // Build a dynamic query with filters
            val response = transaction {
                // Base query with potential joins
                var query = Books
                    .leftJoin(BookAuthors)
                    .leftJoin(Authors)
                    .leftJoin(BookCategories)
                    .leftJoin(Categories)
                    .selectAll()

                // Apply filters conditionally
                title?.let { query = query.andWhere { Books.title like "%$it%" } }
                author?.let { query = query.andWhere { Authors.name like "%$it%" } }
                publisher?.let { query = query.andWhere { Books.publisher like "%$it%" } }
                category?.let { query = query.andWhere { Categories.name like "%$it%" } }
                minPrice?.let { query = query.andWhere { Books.price greaterEq BigDecimal.valueOf(it) } }
                maxPrice?.let { query = query.andWhere { Books.price lessEq BigDecimal.valueOf(it) } }

                // Get total count for pagination
                val totalItems = query.copy().count()

                // Fetch paginated results
                val books = query
                    .limit(limit, offset.toLong())
                    .map {
                        BookDTO(
                            id = it[Books.id],
                            title = it[Books.title],
                            isbn = it[Books.isbn],
                            publisher = it[Books.publisher],
                            publicationDate = it[Books.publicationDate]?.toString(),
                            price = it[Books.price].toDouble(),
                            stockLevel = it[Books.stockLevel],
                            description = it[Books.description],
                            productForm = it[Books.productForm],
                            productFormDetail = it[Books.productFormDetail],
                            height = it[Books.height]?.toDouble(),
                            width = it[Books.width]?.toDouble(),
                            weight = it[Books.weight]?.toDouble()
                        )
                    }
                    .distinctBy { it.id } // Avoid duplicates from joins

                PaginatedBooksResponse(
                    books = books,
                    totalItems = totalItems,
                    totalPages = ((totalItems + limit - 1) / limit).toInt(), // Ceiling division
                    currentPage = page,
                    pageSize = limit
                )
            }

            call.respond(response)
        }
    }
}
