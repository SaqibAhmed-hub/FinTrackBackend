package com.example.fintrack.budget.dto

import java.math.BigDecimal
import java.util.UUID

data class BudgetResponse(
    val id: UUID,
    val categoryName: String,
    val year: Int,
    val month: Int,
    val budgetAmount: BigDecimal,
    val spentAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val exceeded: Boolean
)