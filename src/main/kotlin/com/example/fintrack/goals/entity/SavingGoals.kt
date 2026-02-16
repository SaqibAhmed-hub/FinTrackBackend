package com.example.fintrack.goals.entity

import com.example.fintrack.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "savings_goals")
class SavingsGoal(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var targetAmount: BigDecimal,

    @Column(nullable = false)
    var savedAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var targetDate: LocalDate,

    @Column(nullable = false)
    var completed: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)
