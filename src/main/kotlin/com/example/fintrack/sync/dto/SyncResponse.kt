package com.example.fintrack.sync.dto

import com.example.fintrack.budget.entity.Budget
import com.example.fintrack.categories.entity.Category
import com.example.fintrack.debt.entity.Debt
import com.example.fintrack.goals.entity.SavingsGoal
import com.example.fintrack.transaction.entity.Transaction
import java.time.LocalDateTime

data class SyncResponse(
    val categories: List<Category>,
    val transactions: List<Transaction>,
    val budgets: List<Budget>,
    val goals: List<SavingsGoal>,
    val debts: List<Debt>,
    val serverTime: LocalDateTime
)
