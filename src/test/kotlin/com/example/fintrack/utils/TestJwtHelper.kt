package com.example.fintrack.utils


import com.example.fintrack.security.JwtTokenProvider
import com.example.fintrack.user.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class TestJwtHelper(
    private val jwtService: JwtTokenProvider,
    private val userRepository: UserRepository
) {

    fun generateToken(email: String): String {
        val user = userRepository.findByEmailAndIsDeletedFalse(email)
            ?: throw RuntimeException("User not found")

        return jwtService.generateToken(user.id!!)
    }
}
