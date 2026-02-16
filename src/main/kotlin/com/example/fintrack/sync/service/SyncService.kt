package com.example.fintrack.sync.service

import com.example.fintrack.budget.repository.BudgetRepository
import com.example.fintrack.categories.repository.CategoryRepository
import com.example.fintrack.debt.repository.DebtRepository
import com.example.fintrack.goals.repository.SavingsGoalsRepository
import com.example.fintrack.sync.dto.SyncResponse
import com.example.fintrack.transaction.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class SyncService(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: SavingsGoalsRepository,
    private val debtRepository: DebtRepository
) {

    fun sync(userId: UUID, lastSync: LocalDateTime): SyncResponse {

        return SyncResponse(
            categories = categoryRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync),

            transactions = transactionRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync),

            budgets = budgetRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync),

            goals = goalRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync),

            debts = debtRepository
                .findByUserIdAndUpdatedAtAfter(userId, lastSync),

            serverTime = LocalDateTime.now()
        )
    }
}
