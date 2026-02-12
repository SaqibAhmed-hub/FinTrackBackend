package com.example.fintrack.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password_hash: String?,

    @Column(nullable = false)
    var default_currency: String,

    @Column(nullable = false)
    var role: String = "USER",

    var created_at: LocalDateTime = LocalDateTime.now(),

    var updated_at: LocalDateTime = LocalDateTime.now(),

    var isDeleted: Boolean = false
)