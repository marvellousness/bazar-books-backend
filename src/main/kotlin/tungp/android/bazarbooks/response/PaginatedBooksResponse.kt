package tungp.android.bazarbooks.response

import kotlinx.serialization.Serializable
import tungp.android.bazarbooks.models.Book

@Serializable
data class PaginatedBooksResponse(
    val books: List<Book>,
    val totalItems: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)