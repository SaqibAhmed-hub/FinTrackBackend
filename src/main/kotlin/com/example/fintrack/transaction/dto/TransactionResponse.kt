package com.example.fintrack.transaction.dto

import com.example.fintrack.transaction.entity.PaymentMethod
import com.example.fintrack.transaction.entity.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val amount: BigDecimal,
    val description: String,
    val type: TransactionType,
    val paymentMethod: PaymentMethod,
    val categoryName: String,
    val createdAt: LocalDateTime
)
