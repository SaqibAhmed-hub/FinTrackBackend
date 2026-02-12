package com.example.fintrack.auth.service




import com.example.fintrack.auth.dto.LoginRequest
import com.example.fintrack.auth.dto.RegisterRequest
import com.example.fintrack.security.JwtTokenProvider
import com.example.fintrack.user.entity.User
import com.example.fintrack.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun register(request: RegisterRequest) {

        val existingUser = userRepository.findByEmailAndIsDeletedFalse(request.email)
        if (existingUser != null) {
            throw RuntimeException("Email already registered")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password_hash = passwordEncoder.encode(request.password),
            default_currency = request.defaultCurrency,
            created_at = LocalDateTime.now(),
            updated_at = LocalDateTime.now()
        )

        userRepository.save(user)
    }

    fun login(request: LoginRequest): Map<String, Any> {

        val user = userRepository.findByEmailAndIsDeletedFalse(request.email)
            ?: throw RuntimeException("Invalid credentials")

        if (!passwordEncoder.matches(request.password, user.password_hash)) {
            throw RuntimeException("Invalid credentials")
        }

        val token = jwtTokenProvider.generateToken(user.id!!)

        return mapOf(
            "accessToken" to token,
            "expiresIn" to 3600
        )
    }
}
