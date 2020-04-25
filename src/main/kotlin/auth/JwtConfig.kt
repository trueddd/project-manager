package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import utils.AppEnvironment
import java.time.Duration
import java.util.*

object JwtConfig {

    private val validityInMs = Duration.ofHours(24).toMillis()
    private val algorithm = Algorithm.HMAC512(AppEnvironment.JWT.secret())

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(AppEnvironment.JWT.issuer())
            .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(userId: Int): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(AppEnvironment.JWT.issuer())
            .withClaim("id", userId)
            .withExpiresAt(getExpiration())
            .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}