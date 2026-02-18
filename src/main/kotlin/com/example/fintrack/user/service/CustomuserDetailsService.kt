package com.example.fintrack.user.service

import com.example.fintrack.user.dto.CustomUserDetails
import com.example.fintrack.user.dto.UserResponse
import com.example.fintrack.user.entity.User
import com.example.fintrack.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmailAndIsDeletedFalse(email)
            ?: throw UsernameNotFoundException("User not found")

        return CustomUserDetails(
            id = user.id!!,
            email = user.email,
            password = user.password_hash ?: "",
            authorities = emptyList()
        )
    }

    fun loadUserById(userId: UUID): UserDetails {

        val user = userRepository.findById(userId)
            .orElseThrow { UsernameNotFoundException("User not found") }

        return CustomUserDetails(
            id = user.id!!,
            email = user.email,
            password = user.password_hash ?: "",
            authorities = emptyList()
        )
    }

    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("User not authenticated")

        val principal = authentication.principal as? CustomUserDetails
            ?: throw IllegalStateException("User not authenticated")

        return userRepository.findById(principal.id)
            .orElseThrow { RuntimeException("User not found") }
    }

    fun deleteUser(userId: UUID, currentUser: CustomUserDetails) {

        // Allow user to delete only their own account
        if (userId != currentUser.id) {
            throw IllegalArgumentException("You are not allowed to delete this user")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Soft delete (recommended)
        user.isDeleted = true
        userRepository.save(user)

    }

    fun getProfileUser(user: CustomUserDetails): UserResponse {

        val profileUser = userRepository.findById(user.id)
            .orElseThrow { UsernameNotFoundException("User not found") }

        return UserResponse(
            id = profileUser.id!!,
            email = profileUser.email,
            fullName = profileUser.name
        )
    }
}
