package tungp.android.bazarbooks.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String,
    val createdAt: LocalDateTime
)

fun ResultRow.toUser() = User(
    id = this[Users.id],
    username = this[Users.username],
    email = this[Users.email],
    passwordHash = this[Users.passwordHash],
    role = this[Users.role],
    createdAt = this[Users.createdAt]
)