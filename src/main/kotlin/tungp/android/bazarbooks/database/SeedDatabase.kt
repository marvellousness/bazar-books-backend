package tungp.android.bazarbooks.database

import org.jetbrains.exposed.sql.insert

fun seedDatabase() {
    // Insert sample categories
    val categoryIds = mutableListOf<Int>()
    Categories.insert {
        it[Categories.name] = "Fiction"
        it[Categories.description] = "Fictional books"
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Non-Fiction"
        it[Categories.description] = "Non-fictional books"
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Science Fiction"
        it[Categories.description] = "Books about imagined futures, science, and technology."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Fantasy"
        it[Categories.description] = "Books set in imaginary worlds, often with magic or supernatural elements."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Mystery"
        it[Categories.description] = "Books that revolve around solving a crime or puzzle."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Thriller"
        it[Categories.description] = "Fast-paced, suspenseful books."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Romance"
        it[Categories.description] = "Books centered around a romantic relationship."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Horror"
        it[Categories.description] = "Books designed to frighten, scare, or disgust."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "History"
        it[Categories.description] = "Books about past events."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Biography"
        it[Categories.description] = "A book about a person's life written by someone else."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Science"
        it[Categories.description] = "Books on scientific topics."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }

    Categories.insert {
        it[Categories.name] = "Technology"
        it[Categories.description] = "Books about technology and engineering."
    }.resultedValues?.first()?.get(Categories.id)?.let { id -> categoryIds.add(id) }


    // Insert sample users
    val aliceRes = Users.insert {
        it[Users.username] = "alice"
        it[Users.email] = "alice@example.com"
        it[Users.passwordHash] = "hashed_password_1"
        it[Users.role] = "customer"
    }
    val userId = aliceRes[Users.id]

    val bobRes = Users.insert {
        it[Users.username] = "bob"
        it[Users.email] = "bob@example.com"
        it[Users.passwordHash] = "hashed_password_2"
        it[Users.role] = "customer"
    }
    val user2Id = bobRes[Users.id]


    // Demo Authors
    val authorIds = mutableListOf<Int>()
    for (i in 1..7) {
        val authorId = Authors.insert {
            it[name] = "Author $i"
            it[biography] = "Biography for author $i."
            it[profileImageUrl] = null
            it[nationality] = "Nationality $i"
            it[birthDate] = "198$i-01-01"
            it[isVerified] = i % 2 == 0
        }[Authors.id]
        authorIds.add(authorId)
    }

    // Demo Vendors
    for (i in 1..5) {
        Vendors.insert {
            it[name] = "Vendor $i"
            it[description] = "Description for vendor $i."
            it[logoUrl] = null
            it[websiteUrl] = "https://vendor$i.example.com"
            it[rating] = (3.0 + i * 0.2).toFloat()
            it[totalReviews] = (50 + i * 10).toFloat()
            it[isVerified] = i % 2 != 0
            it[isActive] = true
        }
    }

    // Demo SpecialOffers
    for (i in 1..5) {
        SpecialOffers.insert {
            it[title] = "Offer $i"
            it[description] = "Description for offer $i."
            it[discountPercentage] = (10 + i * 2).toFloat()
            it[discountAmount] = null
            it[maxUsageCount] = 100 + i * 10
            it[currentUsageCount] = 5 + i
        }
    }

    // Insert 30 sample books with random categories
    val bookIds = mutableListOf<Int>()
    for (i in 1..30) {
        val randomCategoryId = categoryIds.random()
        val bookId = Books.insert {
            it[Books.title] = "Sample Book $i"
            it[Books.author] = "Author $i"
            it[Books.isbn] = "ISBN0000$i"
            it[Books.price] = java.math.BigDecimal.valueOf(10 + i.toDouble())
            it[Books.stock] = 5 + i
            it[Books.description] = "Description for sample book $i."
            it[Books.categoryId] = randomCategoryId
            it[Books.authorId] = authorIds.random()
        }[Books.id]
        bookIds.add(bookId)
    }


    // Add cart items
    CartItems.insert {
        it[CartItems.userId] = userId
        it[CartItems.bookId] = bookIds.first()
        it[CartItems.quantity] = 2
        it[CartItems.price] = java.math.BigDecimal("19.99")
    }
    CartItems.insert {
        it[CartItems.userId] = userId
        it[CartItems.bookId] = bookIds.last()
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
        it[OrderItems.bookId] = bookIds.first()
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
        it[Wishlists.bookId] = bookIds.last()
    }
}