package tungp.android.bazarbooks.database

import org.jetbrains.exposed.sql.insert

fun seedDatabase() {
    // Insert sample categories
    val categories = listOf(
        mapOf("name" to "Fiction", "description" to "Novels, short stories, and other imaginative literature", "coverUrl" to "https://images.unsplash.com/photo-1544947950-fa07a98d237f?q=80&w=387&auto=format&fit=crop"),
        mapOf("name" to "Non-Fiction", "description" to "Factual literature based on real events, people, and information", "coverUrl" to "https://images.unsplash.com/photo-1589998059171-988d887df646?q=80&w=1776&auto=format&fit=crop"),
        mapOf("name" to "Science Fiction", "description" to "Fiction with imaginative concepts like futuristic science and technology", "coverUrl" to "https://images.unsplash.com/photo-1518497927675-245083503cdc?q=80&w=1770&auto=format&fit=crop"),
        mapOf("name" to "Mystery", "description" to "Fiction dealing with the solution of a crime or the unraveling of secrets", "coverUrl" to "https://images.unsplash.com/photo-1567301074221-5c754f2fb48e?q=80&w=1771&auto=format&fit=crop"),
        mapOf("name" to "Romance", "description" to "Stories centered on romantic relationships and love", "coverUrl" to "https://images.unsplash.com/photo-1474552226712-ac0f0961a954?q=80&w=1771&auto=format&fit=crop"),
        mapOf("name" to "Fantasy", "description" to "Fiction featuring magical and supernatural elements", "coverUrl" to "https://images.unsplash.com/photo-1520209759809-a9bcb6cb3241?q=80&w=1888&auto=format&fit=crop"),
        mapOf("name" to "Biography", "description" to "Detailed description of a person's life and achievements", "coverUrl" to "https://images.unsplash.com/photo-1516979187457-637abb4f9353?q=80&w=1770&auto=format&fit=crop"),
        mapOf("name" to "History", "description" to "Books focusing on past events and their implications", "coverUrl" to "https://images.unsplash.com/photo-1461360370896-922624d12aa1?q=80&w=1774&auto=format&fit=crop"),
        mapOf("name" to "Self-Help", "description" to "Books offering advice for personal improvement", "coverUrl" to "https://images.unsplash.com/photo-1535572290543-960a8046f5af?q=80&w=1770&auto=format&fit=crop"),
        mapOf("name" to "Thriller", "description" to "Fiction designed to hold tension and excitement for readers", "coverUrl" to "https://images.unsplash.com/photo-1543847473-dc60889684ec?q=80&w=1887&auto=format&fit=crop"),
        mapOf("name" to "Business", "description" to "Books about entrepreneurship, management, and corporate strategies", "coverUrl" to "https://images.unsplash.com/photo-1553028826-cc8584e3e767?q=80&w=1770&auto=format&fit=crop"),
        mapOf("name" to "Science", "description" to "Books about scientific discoveries, theories, and research", "coverUrl" to "https://images.unsplash.com/photo-1507413245164-6160d8298b31?q=80&w=1770&auto=format&fit=crop"),
        mapOf("name" to "Travel", "description" to "Books about destinations, cultures, and travel experiences", "coverUrl" to "https://images.unsplash.com/photo-1499591934245-40b55745b905?q=80&w=1772&auto=format&fit=crop"),
        mapOf("name" to "Cooking", "description" to "Books featuring recipes, cooking techniques, and culinary journeys", "coverUrl" to "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?q=80&w=1770&auto=format&fit=crop"),
        mapOf("name" to "Comics & Graphic Novels", "description" to "Illustrated stories and narratives in sequential art form", "coverUrl" to "https://images.unsplash.com/photo-1608889476518-53c1fa250402?q=80&w=1780&auto=format&fit=crop")
    )

    val categoryIds = categories.map { category ->
        Categories.insert {
            it[name] = category["name"]!!
            it[description] = category["description"]!!
            it[coverUrl] = category["coverUrl"]!!
        }[Categories.id]
    }

    val categoryId = categoryIds[0]
    val catNonFictionId = categoryIds[1]

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
    val authors = listOf(
        mapOf("name" to "Stephen King", "profileImageUrl" to "https://covers.openlibrary.org/a/id/7127409-M.jpg"),
        mapOf("name" to "George R. Martin", "profileImageUrl" to "https://covers.openlibrary.org/a/id/6387401-M.jpg"),
        mapOf("name" to "E. L. James", "profileImageUrl" to "https://covers.openlibrary.org/a/id/14362742-M.jpg"),
        mapOf("name" to "Agatha Christie", "profileImageUrl" to "https://covers.openlibrary.org/b/id/505653-M.jpg"),
        mapOf("name" to "E. B. White", "profileImageUrl" to "https://covers.openlibrary.org/a/id/6390049-M.jpg"),
        mapOf("name" to "Neil Gaiman", "profileImageUrl" to "https://covers.openlibrary.org/a/id/7277125-M.jpg")
    )

    val biographies = listOf(
        "An influential author in the horror genre.",
        "Creator of epic fantasy worlds.",
        "Known for captivating romance novels.",
        "The queen of mystery fiction.",
        "Beloved author of children's classics.",
        "Master of modern fantasy and mythology."
    )

    val nationalities = listOf("American", "British", "Canadian", "Australian")

    val authorIds = authors.mapIndexed { index, author ->
        Authors.insert {
            it[name] = author["name"] as String
            it[profileImageUrl] = author["profileImageUrl"] as String
            it[biography] = biographies.getOrNull(index) ?: "A prolific author."
            it[nationality] = nationalities.random()
            it[birthDate] = "19${(50..99).random()}-${(1..12).random().toString().padStart(2, '0')}-${(1..28).random().toString().padStart(2, '0')}"
            it[isVerified] = true
        }[Authors.id]
    }

    val author1Id = authorIds[0]
    val author2Id = authorIds[1]

    // Demo Vendors
    val vendors = listOf(
        mapOf("name" to "Emma Johnson", "logoUrl" to "https://covers.openlibrary.org/b/id/9171544-L.jpg", "rating" to 5, "description" to "Bookworm Paradise"),
        mapOf("name" to "David Garcia", "logoUrl" to "https://covers.openlibrary.org/b/id/12816871-L.jpg", "rating" to 4, "description" to "Literary Haven"),
        mapOf("name" to "Sophia Martinez", "logoUrl" to "https://covers.openlibrary.org/b/id/12645178-L.jpg", "rating" to 5, "description" to "Poetic Dreams"),
        mapOf("name" to "Michael Brown", "logoUrl" to "https://covers.openlibrary.org/b/id/12713304-L.jpg", "rating" to 4, "description" to "Verse Valley"),
        mapOf("name" to "Olivia Wilson", "logoUrl" to "https://covers.openlibrary.org/b/id/12740772-L.jpg", "rating" to 4, "description" to "Stationery Stars"),
        mapOf("name" to "James Taylor", "logoUrl" to "https://covers.openlibrary.org/b/id/12577577-L.jpg", "rating" to 5, "description" to "Paper Paradise"),
        mapOf("name" to "Emily Adams", "logoUrl" to "https://covers.openlibrary.org/b/id/12851679-L.jpg", "rating" to 3, "description" to "Special Selections"),
        mapOf("name" to "Daniel Lee", "logoUrl" to "https://covers.openlibrary.org/b/id/12810671-L.jpg", "rating" to 4, "description" to "Paperback Palace"),
        mapOf("name" to "Ava Wright", "logoUrl" to "https://covers.openlibrary.org/b/id/12810709-L.jpg", "rating" to 5, "description" to "Poetic Wonders"),
        mapOf("name" to "Noah Parker", "logoUrl" to "https://covers.openlibrary.org/b/id/12697617-L.jpg", "rating" to 4, "description" to "Premium Picks"),
        mapOf("name" to "Isabella King", "logoUrl" to "https://covers.openlibrary.org/b/id/13293386-L.jpg", "rating" to 5, "description" to "Stationery Shop"),
        mapOf("name" to "William Green", "logoUrl" to "https://covers.openlibrary.org/b/id/10543646-L.jpg", "rating" to 4, "description" to "Chapter Corner")
    )

    vendors.forEach { vendor ->
        Vendors.insert {
            it[name] = vendor["name"] as String
            it[description] = vendor["description"] as String
            it[logoUrl] = vendor["logoUrl"] as String
            it[websiteUrl] = "https://www.${(vendor["name"] as String).replace(" ", "").lowercase()}.com"
            it[rating] = (vendor["rating"] as Int).toFloat()
            it[totalReviews] = (50..500).random().toFloat()
            it[isVerified] = true
            it[isActive] = true
        }
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