package com.example.fintrack.user.dto

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val fullName: String
)