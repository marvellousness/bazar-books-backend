package tungp.android.bazarbooks.database

import org.jetbrains.exposed.sql.insert

fun seedDatabase() {
    // Insert sample categories
    val fictionRes = Categories.insert {
        it[Categories.name] = "Fiction"
        it[Categories.description] = "Fictional books"
    }
    val categoryId = fictionRes[Categories.id]

    val nonfictionRes = Categories.insert {
        it[Categories.name] = "Non-Fiction"
        it[Categories.description] = "Non-fictional books"
    }
    val catNonFictionId = nonfictionRes[Categories.id]

    // Insert sample users
    val aliceRes = Users.insert {
        it[Users.username] = "alice"
        it[Users.email] = "alice@example.com"
        it[Users.passwordHash] = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "password123".toCharArray())
        it[Users.role] = "customer"
    }
    val userId = aliceRes[Users.id]

    val bobRes = Users.insert {
        it[Users.username] = "bob"
        it[Users.email] = "bob@example.com"
        it[Users.passwordHash] = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "password123".toCharArray())
        it[Users.role] = "customer"
    }
    val user2Id = bobRes[Users.id]


    // Demo Authors
    val author1Id = Authors.insert {
        it[name] = "Dmitry Jemerov"
        it[biography] = "Lead Kotlin at JetBrains."
        it[profileImageUrl] = null
        it[nationality] = "Russian"
        it[birthDate] = "1977-06-10"
        it[isVerified] = true
    }[Authors.id]
    val author2Id = Authors.insert {
        it[name] = "Joshua Bloch"
        it[biography] = "Legendary Java developer."
        it[profileImageUrl] = null
        it[nationality] = "American"
        it[birthDate] = "1961-08-28"
        it[isVerified] = true
    }[Authors.id]

    // Demo Vendors
    Vendors.insert {
        it[name] = "BookMart"
        it[description] = "Top-rated book vendor."
        it[logoUrl] = null
        it[websiteUrl] = null
        it[rating] = 4.8f
        it[totalReviews] = 154f
        it[isVerified] = true
        it[isActive] = true
    }
    Vendors.insert {
        it[name] = "LibraryZone"
        it[description] = "Quality books provider."
        it[logoUrl] = null
        it[websiteUrl] = "https://libraryzone.example.com"
        it[rating] = 4.5f
        it[totalReviews] = 97f
        it[isVerified] = false
        it[isActive] = true
    }

    // Demo SpecialOffers
    SpecialOffers.insert {
        it[title] = "Spring Sale 20% Off"
        it[description] = "Save big on selected books this spring!"
        it[discountPercentage] = 20f
        it[discountAmount] = null
        it[maxUsageCount] = 100
        it[currentUsageCount] = 5
    }
    SpecialOffers.insert {
        it[title] = "Best Seller Discount"
        it[description] = "10% off all best-sellers!"
        it[discountPercentage] = 10f
        it[discountAmount] = null
        it[maxUsageCount] = null
        it[currentUsageCount] = 0
    }

    // Insert sample books
    val book1Res = Books.insert {
        it[Books.title] = "Kotlin for Beginners"
        it[Books.author] = "John Author"
        it[Books.isbn] = "1234567890"
        it[Books.price] = java.math.BigDecimal("19.99")
        it[Books.stock] = 10
        it[Books.description] = "A beginner's guide to Kotlin."
        it[Books.categoryId] = categoryId
        it[Books.authorId] = author1Id
    }
    val bookId = book1Res[Books.id]

    val book2Res = Books.insert {
        it[Books.title] = "Learning Java"
        it[Books.author] = "Jane Developer"
        it[Books.isbn] = "0987654321"
        it[Books.price] = java.math.BigDecimal("29.99")
        it[Books.stock] = 5
        it[Books.description] = "Comprehensive Java teachings."
        it[Books.categoryId] = catNonFictionId
        it[Books.authorId] = author1Id
    }
    val book2Id = book2Res[Books.id]

    // Insert additional books for pagination (>20 total)
    for (i in 3..23) {
        Books.insert {
            it[Books.title] = "Sample Book $i"
            it[Books.author] = "Author $i"
            it[Books.isbn] = "ISBN0000$i"
            it[Books.price] = java.math.BigDecimal.valueOf(10 + i.toDouble())
            it[Books.stock] = 5 + i
            it[Books.description] = "Description for sample book $i."
            it[Books.categoryId] = if (i % 2 == 0) categoryId else catNonFictionId
            it[Books.authorId] = author1Id
        }
    }

    // Add cart items
    CartItems.insert {
        it[CartItems.userId] = userId
        it[CartItems.bookId] = bookId
        it[CartItems.quantity] = 2
        it[CartItems.price] = java.math.BigDecimal("19.99")
    }
    CartItems.insert {
        it[CartItems.userId] = user2Id
        it[CartItems.bookId] = book2Id
        it[CartItems.quantity] = 1
        it[CartItems.price] = java.math.BigDecimal("29.99")
    }

    // Sample orders
    val orderRes = Orders.insert {
        it[Orders.userId] = userId
        it[Orders.totalAmount] = java.math.BigDecimal("39.98")
        it[Orders.status] = "COMPLETED"
        it[Orders.shippingAddress] = "123 Main St, Cityville"
        it[Orders.paymentMethod] = "Credit Card"
    }
    val orderId = orderRes[Orders.id]

    // Order items
    OrderItems.insert {
        it[OrderItems.orderId] = orderId
        it[OrderItems.bookId] = bookId
        it[OrderItems.quantity] = 2
        it[OrderItems.price] = java.math.BigDecimal("19.99")
        it[OrderItems.bookTitle] = "Kotlin for Beginners"
    }

    // Payment for order
    Payments.insert {
        it[Payments.orderId] = orderId
        it[Payments.amount] = java.math.BigDecimal("39.98")
        it[Payments.paymentMethod] = "Credit Card"
        it[Payments.status] = "COMPLETED"
        it[Payments.transactionId] = "txn123"
    }

    // Wishlists
    Wishlists.insert {
        it[Wishlists.userId] = user2Id
        it[Wishlists.bookId] = bookId
    }


}