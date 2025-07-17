package tungp.android.bazarbooks.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.database.*
import tungp.android.bazarbooks.models.*

class HomeFeedsService {
    fun getHomeFeeds(): HomeFeeds = transaction {
        val bestOffers = SpecialOffers.selectAll().limit(5).map { toSpecialOffer(it) }
        val topOfWeeks = Books.selectAll().limit(7).map { toBook(it) }
        val bestVendors = Vendors.selectAll().limit(5).map { toVendor(it) }
        val bestAuthors = Authors.selectAll().limit(7).map { toAuthor(it) }
        HomeFeeds(
            bestOffers = bestOffers,
            topOfWeeks = topOfWeeks,
            bestVendors = bestVendors,
            bestAuthors = bestAuthors
        )
    }

    private fun toSpecialOffer(row: ResultRow) = SpecialOffer(
        id = row[SpecialOffers.id],
        title = row[SpecialOffers.title],
        description = row[SpecialOffers.description],
        discountPercentage = row[SpecialOffers.discountPercentage],
        discountAmount = row[SpecialOffers.discountAmount],
        maxUsageCount = row[SpecialOffers.maxUsageCount],
        currentUsageCount = row[SpecialOffers.currentUsageCount]
    )

    private fun toBook(row: ResultRow) = Book(
        id = row[Books.id],
        title = row[Books.title],
        author = row[Books.author],
        isbn = row[Books.isbn],
        price = row[Books.price].toDouble(),
        stock = row[Books.stock],
        description = row[Books.description],
        categoryId = row[Books.categoryId],
        authorId = row[Books.authorId],
        createdAt = row[Books.createdAt].toString(),
        cover = row[Books.cover],
        rating = row[Books.rating]
    )

    private fun toVendor(row: ResultRow) = Vendor(
        id = row[Vendors.id],
        name = row[Vendors.name],
        description = row[Vendors.description],
        logoUrl = row[Vendors.logoUrl],
        websiteUrl = row[Vendors.websiteUrl],
        rating = row[Vendors.rating],
        totalReviews = row[Vendors.totalReviews],
        isVerified = row[Vendors.isVerified],
        isActive = row[Vendors.isActive]
    )

    private fun toAuthor(row: ResultRow) = Author(
        id = row[Authors.id],
        name = row[Authors.name],
        biography = row[Authors.biography],
        profileImageUrl = row[Authors.profileImageUrl],
        nationality = row[Authors.nationality],
        birthDate = row[Authors.birthDate],
        isVerified = row[Authors.isVerified]
    )
}
