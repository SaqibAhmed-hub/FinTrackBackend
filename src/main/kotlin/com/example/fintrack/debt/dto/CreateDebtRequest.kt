package com.example.fintrack.debt.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CreateDebtRequest(
    val name: String,
    val totalAmount: BigDecimal,
    val interestRate: Double,
    val dueDate: LocalDate
)
