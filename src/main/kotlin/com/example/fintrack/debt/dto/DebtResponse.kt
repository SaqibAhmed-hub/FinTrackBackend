package com.example.fintrack.debt.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class DebtResponse(
    val id: UUID,
    val name: String,
    val totalAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val interestRate: Double,
    val dueDate: LocalDate,
    val progressPercentage: Double,
    val closed: Boolean
)
