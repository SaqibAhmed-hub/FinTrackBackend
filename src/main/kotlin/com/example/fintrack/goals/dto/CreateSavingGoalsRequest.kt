package com.example.fintrack.goals.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CreateSavingsGoalRequest(
    val name: String,
    val targetAmount: BigDecimal,
    val targetDate: LocalDate
)