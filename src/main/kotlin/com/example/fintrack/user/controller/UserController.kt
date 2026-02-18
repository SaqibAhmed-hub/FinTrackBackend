package com.example.fintrack.user.controller

import com.example.fintrack.user.dto.CustomUserDetails
import com.example.fintrack.user.dto.UserResponse
import com.example.fintrack.user.entity.User
import com.example.fintrack.user.service.CustomUserDetailsService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Users")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userDetailsService: CustomUserDetailsService
) {


    @GetMapping("/me")
    fun getProfileCurrentUser(
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userDetailsService.getProfileUser(user))
    }


    @DeleteMapping("/{id}")
    fun deleteUser(
        @PathVariable id: UUID,
        @AuthenticationPrincipal currentUser: CustomUserDetails
    ): ResponseEntity<Void> {

        userDetailsService.deleteUser(id, currentUser)
        return ResponseEntity.noContent().build()
    }



}

