package tungp.android.bazarbooks.services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.database.Books
import tungp.android.bazarbooks.models.Book
import java.time.format.DateTimeFormatter

class BookService {
    fun getAllBooks(): List<Book> = transaction {
        Books.selectAll().map { row -> rowToBook(row) }
    }

    fun getBooksPaginated(page: Int, pageSize: Int): Pair<List<Book>, Long> = transaction {
        val totalItems = Books.selectAll().count()
        val books = Books
            .selectAll()
            .limit(pageSize, offset = ((page - 1) * pageSize).toLong())
            .map { rowToBook(it) }
        books to totalItems
    }

    fun getBookById(id: Int): Book? = transaction {
        Books.select { Books.id eq id }
            .map { rowToBook(it) }
            .singleOrNull()
    }

    fun addBook(book: Book): Book = transaction {
        val result = Books.insert {
            it[title] = book.title
            it[author] = book.author
            it[isbn] = book.isbn
            it[price] = book.price.toBigDecimal()
            it[stock] = book.stock
            it[description] = book.description
            it[categoryId] = book.categoryId
            it[authorId] = book.authorId
            it[coverUrl] = book.coverUrl
            it[publishedYear] = book.publishedYear
            it[publisher] = book.publisher
            it[numberOfPages] = book.numberOfPages
            it[rating] = book.rating
        }
        val insertedId = result[Books.id]
        getBookById(insertedId)!!
    }

    fun updateBook(id: Int, book: Book): Boolean = transaction {
        Books.update({ Books.id eq id }) {
            it[title] = book.title
            it[author] = book.author
            it[isbn] = book.isbn
            it[price] = book.price.toBigDecimal()
            it[stock] = book.stock
            it[description] = book.description
            it[categoryId] = book.categoryId
            it[authorId] = book.authorId
            it[coverUrl] = book.coverUrl
            it[publishedYear] = book.publishedYear
            it[publisher] = book.publisher
            it[numberOfPages] = book.numberOfPages
            it[rating] = book.rating
        } > 0
    }

    fun deleteBook(id: Int): Boolean = transaction {
        Books.deleteWhere { Books.id eq id } > 0
    }

    private fun rowToBook(row: ResultRow): Book = Book(
        id = row[Books.id],
        title = row[Books.title],
        author = row[Books.author],
        isbn = row[Books.isbn],
        price = row[Books.price].toDouble(),
        stock = row[Books.stock],
        authorId = row[Books.authorId],
        description = row[Books.description],
        categoryId = row[Books.categoryId],
        coverUrl = row[Books.coverUrl],
        publishedYear = row[Books.publishedYear],
        publisher = row[Books.publisher],
        numberOfPages = row[Books.numberOfPages],
        rating = row[Books.rating],
        createdAt = row[Books.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}
