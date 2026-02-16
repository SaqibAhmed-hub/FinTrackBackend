package com.example.fintrack.budget.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateBudgetRequest(
    val categoryId: UUID,
    val year: Int,
    val month: Int,
    val amount: BigDecimal
)
