package com.example.fintrack.user.repository

import com.example.fintrack.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmailAndIsDeletedFalse(email: String): User?
}