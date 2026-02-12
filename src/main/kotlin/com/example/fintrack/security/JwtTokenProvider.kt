package com.example.fintrack.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {

    private lateinit var key: SecretKeySpec

    @PostConstruct
    fun init() {
        key = SecretKeySpec(
            jwtProperties.secret.toByteArray(),
            SignatureAlgorithm.HS256.jcaName
        )
    }

    fun generateToken(userId: UUID): String {

        val now = Date()
        val validity = Date(now.time + jwtProperties.expiration)

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key,SignatureAlgorithm.HS256)
            .compact()
    }

    fun getUserId(token: String): UUID {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return UUID.fromString(claims.subject)
    }


    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}
