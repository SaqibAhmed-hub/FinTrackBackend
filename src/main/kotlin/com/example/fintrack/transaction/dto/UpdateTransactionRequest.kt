package com.example.fintrack.transaction.dto

import com.example.fintrack.transaction.entity.TransactionType
import java.math.BigDecimal
import java.util.UUID

data class UpdateTransactionRequest(
    val amount: BigDecimal,
    val description: String,
    val type: TransactionType,
    val categoryId: UUID
)