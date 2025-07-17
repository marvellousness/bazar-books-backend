package tungp.android.bazarbooks.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.database.Categories
import tungp.android.bazarbooks.models.Category

class CategoryService {
    fun getAllCategories(): List<Category> = transaction {
        Categories.selectAll().map { toCategory(it) }
    }

    private fun toCategory(row: ResultRow) = Category(
        id = row[Categories.id],
        name = row[Categories.name],
        description = row[Categories.description]
    )
}