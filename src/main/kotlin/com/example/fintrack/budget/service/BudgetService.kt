package com.example.fintrack.budget.service

import com.example.fintrack.budget.dto.BudgetResponse
import com.example.fintrack.budget.dto.CreateBudgetRequest
import com.example.fintrack.budget.dto.UpdateBudgetRequest
import com.example.fintrack.budget.entity.Budget
import com.example.fintrack.budget.repository.BudgetRepository
import com.example.fintrack.categories.repository.CategoryRepository
import com.example.fintrack.transaction.repository.TransactionRepository
import com.example.fintrack.user.service.CustomUserDetailsService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@Service
class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val userService: CustomUserDetailsService
) {

    fun createBudget(request: CreateBudgetRequest): BudgetResponse {

        val user = userService.getCurrentUser()

        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { RuntimeException("Category not found") }

        if (budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
                user.id!!, request.categoryId, request.year, request.month
            ) != null
        ) {
            throw RuntimeException("Budget already exists for this month")
        }

        val budget = Budget(
            year = request.year,
            month = request.month,
            amount = request.amount,
            category = category,
            user = user
        )

        val saved = budgetRepository.save(budget)

        return mapToResponse(saved)
    }

    fun getMonthlyBudgets(year: Int, month: Int): List<BudgetResponse> {

        val user = userService.getCurrentUser()

        return budgetRepository
            .findByUserIdAndYearAndMonthAndIsDeletedFalse(user.id!!, year, month)
            .map { mapToResponse(it) }
    }

    fun updateBudget(
        userId: UUID,
        budgetId: UUID,
        request: UpdateBudgetRequest
    ): BudgetResponse {

        val budget = budgetRepository
            .findByIdAndUserIdAndIsDeletedFalse(budgetId, userId)
            ?: throw RuntimeException("Budget not found")

        budget.category.id = request.categoryId
        budget.amount = request.amount
        budget.month = request.month
        budget.year = request.year
        budget.updatedAt = LocalDateTime.now()

        return mapToResponse(budgetRepository.save(budget))

    }

    fun deleteBudget(userId: UUID, budgetId: UUID) {

        val budget = budgetRepository
            .findByIdAndUserIdAndIsDeletedFalse(budgetId, userId)
            ?: throw RuntimeException("Budget not found")

        budget.isDeleted = true
        budget.updatedAt = LocalDateTime.now()

        budgetRepository.save(budget)
    }


    private fun mapToResponse(budget: Budget): BudgetResponse {

        val start = LocalDate.of(budget.year, budget.month, 1).atStartOfDay()
        val end = start.plusMonths(1).minusSeconds(1)

        val spent = transactionRepository.sumExpenseByCategoryAndMonth(
            budget.user.id!!,
            budget.category.id!!,
            start,
            end
        ) ?: BigDecimal.ZERO

        val remaining = budget.amount.subtract(spent)

        return BudgetResponse(
            id = budget.id!!,
            categoryName = budget.category.name,
            year = budget.year,
            month = budget.month,
            budgetAmount = budget.amount,
            spentAmount = spent,
            remainingAmount = remaining,
            exceeded = remaining < BigDecimal.ZERO
        )
    }
}

