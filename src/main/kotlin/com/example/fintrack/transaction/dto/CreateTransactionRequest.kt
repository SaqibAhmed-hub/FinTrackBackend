package com.example.fintrack.transaction.dto

import com.example.fintrack.transaction.entity.PaymentMethod
import com.example.fintrack.transaction.entity.TransactionType
import java.math.BigDecimal
import java.util.UUID

data class CreateTransactionRequest(
    val amount: BigDecimal,
    val description: String,
    val type: TransactionType,
    val paymentMethod: PaymentMethod,
    val categoryId: UUID
)
