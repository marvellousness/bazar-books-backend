package tungp.android.bazarbooks.routes

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tungp.android.bazarbooks.models.ApiResponse
import tungp.android.bazarbooks.models.Book
import tungp.android.bazarbooks.services.BookService
import tungp.android.bazarbooks.response.PaginatedBooksResponse

fun Route.bookRoutes(bookService: BookService) {
    route("/books") {
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 10
            val (books, totalItems) = bookService.getBooksPaginated(page, pageSize)
            val totalPages = if (pageSize == 0) 0 else ((totalItems + pageSize - 1) / pageSize).toInt()
            val response = PaginatedBooksResponse(
                books = books,
                totalItems = totalItems,
                totalPages = totalPages,
                currentPage = page,
                pageSize = pageSize
            )
            call.respond(ApiResponse(true, response, "Fetched books (paginated)"))
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(ApiResponse<Book?>(false, null, "Invalid book id"))
                return@get
            }
            val book = bookService.getBookById(id)
            if (book == null) {
                call.respond(ApiResponse<Book?>(false, null, "Book not found"))
            } else {
                call.respond(ApiResponse(true, book, "Fetched book"))
            }
        }
        post {
            val book = call.receive<Book>()
            val newBook = bookService.addBook(book)
            call.respond(ApiResponse(true, newBook, "Book created successfully"))
        }
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(ApiResponse<Book?>(false, null, "Invalid book id"))
                return@put
            }
            val book = call.receive<Book>()
            val updated = bookService.updateBook(id, book)
            if (updated) {
                call.respond(ApiResponse(true, bookService.getBookById(id), "Book updated successfully"))
            } else {
                call.respond(ApiResponse<Book?>(false, null, "Book not found or not updated"))
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(ApiResponse<Book?>(false, null, "Invalid book id"))
                return@delete
            }
            val deleted = bookService.deleteBook(id)
            if (deleted) {
                call.respond(ApiResponse(true, null, "Book deleted successfully"))
            } else {
                call.respond(ApiResponse<Book?>(false, null, "Book not found or already deleted"))
            }
        }
    }
}
