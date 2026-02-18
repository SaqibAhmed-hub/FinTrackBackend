package com.example.fintrack.auth.controller


import com.example.fintrack.auth.dto.LoginRequest
import com.example.fintrack.auth.dto.RegisterRequest
import com.example.fintrack.auth.service.AuthService
import io.jsonwebtoken.Header
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Authorization")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<Any> {

        authService.register(request)

        return ResponseEntity.status(HttpStatus.CREATED).body( mapOf("message" to "User registered successfully"))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<Any> {

        val response = authService.login(request)

        return ResponseEntity.ok(response)
    }
}
