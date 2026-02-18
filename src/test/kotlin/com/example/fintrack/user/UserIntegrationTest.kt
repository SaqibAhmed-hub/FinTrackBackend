package com.example.fintrack.user

import com.example.fintrack.user.entity.User
import com.example.fintrack.user.repository.UserRepository
import com.example.fintrack.utils.TestJwtHelper
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var jwtService: TestJwtHelper


    @Test
    fun `should register user successfully`() {

        val request = mapOf(
            "name" to "Saqib",
            "email" to "saqib@test.com",
            "password" to "Password123",
            "defaultCurrency" to "INR"
        )

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isCreated() }
            }

        val savedUser = userRepository.findByEmailAndIsDeletedFalse("saqib@test.com")

        assert(savedUser != null)
        assert(passwordEncoder.matches("Password123", savedUser!!.password_hash))
        assert(savedUser.role == "USER")
        assert(!savedUser.isDeleted)
    }

    @Test
    fun `should not allow duplicate email`() {

        userRepository.save(
            User(
                name = "Existing",
                email = "duplicate@test.com",
                password_hash = passwordEncoder.encode("Password123"),
                default_currency = "INR"
            )
        )

        val request = mapOf(
            "name" to "Saqib",
            "email" to "duplicate@test.com",
            "password" to "Password123",
            "defaultCurrency" to "INR"
        )

        mockMvc.post("/api/v1/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `should login and return JWT`() {

        userRepository.save(
            User(
                name = "Saqib",
                email = "login@test.com",
                password_hash = passwordEncoder.encode("Password123"),
                default_currency = "INR"
            )
        )

        val request = mapOf(
            "email" to "login@test.com",
            "password" to "Password123"
        )

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.accessToken") { exists() }
            }
    }

    @Test
    fun `should not login with wrong password`() {

        userRepository.save(
            User(
                name = "Saqib",
                email = "wrong@test.com",
                password_hash = passwordEncoder.encode("CorrectPass"),
                default_currency = "INR"
            )
        )

        val request = mapOf(
            "email" to "wrong@test.com",
            "password" to "WrongPass"
        )

        mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `should return user profile when authenticated`() {

        val user = userRepository.save(
            User(
                name = "Saqib",
                email = "profile@test.com",
                password_hash = passwordEncoder.encode("Password123"),
                default_currency = "INR"
            )
        )

        val token = jwtService.generateToken(user.email)

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer $token")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.email") { value("profile@test.com") }
                jsonPath("$.fullName") { value("Saqib") }
            }
    }

    @Test
    fun `should return 401 when accessing profile without token`() {

        mockMvc.get("/api/v1/users/me")
            .andExpect {
                status { isUnauthorized() }
            }
    }



    @Test
    fun `should soft delete user`() {

        val user = userRepository.save(
            User(
                name = "DeleteUser",
                email = "delete@test.com",
                password_hash = passwordEncoder.encode("Password123"),
                default_currency = "INR"
            )
        )

        val token = jwtService.generateToken(user.email)

        mockMvc.delete("/api/v1/users/${user.id}") {
            header("Authorization", "Bearer $token")
        }
            .andExpect {
                status { isNoContent() }
            }

        val updated = userRepository.findById(user.id!!).get()

        assert(updated.isDeleted)
    }





}