package tungp.android.bazarbooks.auth.service

import at.favre.lib.crypto.bcrypt.BCrypt
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import tungp.android.bazarbooks.auth.data.AuthResponse
import tungp.android.bazarbooks.auth.data.LoginRequest
import tungp.android.bazarbooks.auth.data.RegisterRequest
import tungp.android.bazarbooks.auth.utils.TokenManager
import tungp.android.bazarbooks.database.User
import tungp.android.bazarbooks.database.Users
import tungp.android.bazarbooks.database.toUser
import tungp.android.bazarbooks.models.ApiResponse

class AuthService(private val tokenManager: TokenManager) {

    fun loginUser(request: LoginRequest): ApiResponse<AuthResponse?> {
        return transaction {
            val user = Users.select { Users.email eq request.email }.singleOrNull()

            if (user != null) {
                val passwordMatches = BCrypt.verifyer()
                    .verify(request.passwordHash.toCharArray(), user[Users.passwordHash].toCharArray()).verified
                if (passwordMatches) {
                    val token = tokenManager.generateToken(user[Users.id], user[Users.username], user[Users.email])
                    ApiResponse(
                        true,
                        AuthResponse(token, user[Users.id], user[Users.username], user[Users.email]),
                        "Login successful"
                    )
                } else {
                    ApiResponse(false, null, "Invalid credentials")
                }
            } else {
                ApiResponse(false, null, "User not found")
            }
        }
    }

    fun registerUser(request: RegisterRequest): ApiResponse<AuthResponse?> {
        return transaction {
            // Check if user with email or username already exists
            val existingUser = Users.select {
                (Users.email eq request.email) or (Users.username eq request.username)
            }.singleOrNull()

            if (existingUser != null) {
                ApiResponse(false, null, "User with this email or username already exists")
            } else {
                val hashedPassword = BCrypt.withDefaults().hashToString(12, request.passwordHash.toCharArray())
                val newUserId = Users.insert {
                    it[username] = request.username
                    it[email] = request.email
                    it[passwordHash] = hashedPassword
                    it[role] = "customer"
                } get Users.id

                val token = tokenManager.generateToken(newUserId, request.username, request.email)
                ApiResponse(
                    true,
                    AuthResponse(token, newUserId, request.username, request.email),
                    "Registration successful"
                )
            }
        }
    }

    fun getAllUsers(): ApiResponse<List<User>> {
        return transaction {
            val users = Users.selectAll().map { it.toUser() }
            ApiResponse(true, users, "Users retrieved successfully")
        }
    }
}