package com.example.fintrack.debt.dto

import java.math.BigDecimal

data class AddDebtPaymentRequest(
    val amount: BigDecimal
)