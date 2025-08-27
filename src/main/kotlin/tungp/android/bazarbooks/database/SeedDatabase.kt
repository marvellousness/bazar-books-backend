package tungp.android.bazarbooks.database

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.dao.id.EntityID

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
            it[name] = category["name"] as String
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
    val books = listOf(
        mapOf("coverUrl" to "https://covers.openlibrary.org/b/id/13151170-L.jpg", "description" to "A magical story about following your dreams and listening to your heart.", "isbn" to "9780062315007", "numberOfPages" to 208, "price" to 14.99, "publishedYear" to 1988, "publisher" to "HarperOne", "stock" to 42, "rating" to 4, "title" to "The Alchemist"),
        mapOf("coverUrl" to "https://covers.openlibrary.org/b/id/12634691-L.jpg", "description" to "A story of friendship, betrayal, and redemption set against the backdrop of Afghanistan's tumultuous history.", "isbn" to "9781400033416", "numberOfPages" to 371, "price" to 16.99, "publishedYear" to 2003, "publisher" to "Riverhead Books", "stock" to 27, "rating" to 5, "title" to "The Kite Runner"),
        mapOf("coverUrl" to "https://covers.openlibrary.org/b/id/14416194-L.jpg", "description" to "A tale of two sisters in France during World War II.", "isbn" to "9780143127550", "numberOfPages" to 440, "price" to 18.99, "publishedYear" to 2015, "publisher" to "St. Martin's Press", "stock" to 15, "rating" to 4, "title" to "The Nightingale")
    )

    val bookIds = books.map { book ->
        val randomAuthorIndex = (0 until authorIds.size).random()
        val randomCategoryIndex = (0 until categories.size).random()
        val authorId = authorIds[randomAuthorIndex]
        val categoryId = categoryIds[randomCategoryIndex]
        val authorName = authors[randomAuthorIndex]["name"] as String
        Books.insert {
            it[Books.authorId] = authorId
            it[Books.categoryId] = categoryId
            it[title] = book["title"] as String
            it[author] = authorName
            it[isbn] = book["isbn"] as String
            it[price] = (book["price"] as Double).toBigDecimal()
            it[stock] = book["stock"] as Int
            it[description] = book["description"] as String
            it[coverUrl] = book["coverUrl"] as String
            it[publishedYear] = book["publishedYear"] as Int
            it[publisher] = book["publisher"] as String
            it[numberOfPages] = book["numberOfPages"] as Int
            it[rating] = (book["rating"] as Int).toFloat()
        }[Books.id]
    }

    val bookId = bookIds[0]
    val book2Id = bookIds[1]

    // Add cart items
    CartItems.insert {
        it[CartItems.userId] = userId
        it[CartItems.bookId] = bookId
        it[CartItems.quantity] = 2
        it[CartItems.price] = (books[0]["price"] as Double).toBigDecimal()
    }
    CartItems.insert {
        it[CartItems.userId] = user2Id
        it[CartItems.bookId] = book2Id
        it[CartItems.quantity] = 1
        it[CartItems.price] = (books[1]["price"] as Double).toBigDecimal()
    }

    // Sample orders
    val orderRes = Orders.insert {
        it[Orders.userId] = userId
        it[Orders.totalAmount] = ((books[0]["price"] as Double) * 2).toBigDecimal()
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
        it[OrderItems.price] = (books[0]["price"] as Double).toBigDecimal()
        it[OrderItems.bookTitle] = books[0]["title"] as String
    }

    // Payment for order
    Payments.insert {
        it[Payments.orderId] = orderId
        it[Payments.amount] = ((books[0]["price"] as Double) * 2).toBigDecimal()
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