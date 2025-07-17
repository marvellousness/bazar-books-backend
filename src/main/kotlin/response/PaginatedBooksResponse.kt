package tungp.android.bazarbooks.response

import tungp.android.bazarbooks.dto.BookDTO


data class PaginatedBooksResponse(
    val books: List<BookDTO>,
    val totalItems: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)