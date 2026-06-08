package org.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

/**
 * Configuração centralizada de JWT
 */
object JwtConfig {
    private const val SECRET = "your-secret-key-change-this-in-production-12345678"
    private const val ISSUER = "app-kotlin-api"
    private const val AUDIENCE = "app-users"
    private const val VALIDITY_IN_MS = 36_000_000 // 10 horas

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(email: String): String = JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
        .sign(algorithm)

    fun validateToken(token: String): String? = try {
        JWT.require(algorithm)
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .build()
            .verify(token)
            .getClaim("email")
            .asString()
    } catch (e: Exception) {
        null
    }

    fun getVerifier() = JWT.require(algorithm)
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()
}

