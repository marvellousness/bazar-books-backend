package tungp.android.bazarbooks.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val passwordHash: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val passwordHash: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val username: String,
    val email: String
)
