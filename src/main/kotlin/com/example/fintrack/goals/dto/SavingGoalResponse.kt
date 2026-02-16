package com.example.fintrack.goals.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class SavingsGoalResponse(
    val id: UUID,
    val name: String,
    val targetAmount: BigDecimal,
    val savedAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val progressPercentage: Double,
    val targetDate: LocalDate,
    val completed: Boolean,
    val status: String
)