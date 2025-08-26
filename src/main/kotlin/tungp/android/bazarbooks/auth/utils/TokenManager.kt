package tungp.android.bazarbooks.auth.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import tungp.android.bazarbooks.auth.data.TokenConfig
import java.util.*

class TokenManager(private val config: TokenConfig) {

    fun generateToken(userId: Int, username: String, email: String): String {
        return JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
            .sign(Algorithm.HMAC256(config.secret))
    }

    fun verifyToken(token: String): Boolean {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()
            verifier.verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}