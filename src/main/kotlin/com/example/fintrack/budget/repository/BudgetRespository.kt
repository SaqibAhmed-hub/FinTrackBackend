package com.example.fintrack.budget.repository

import com.example.fintrack.budget.entity.Budget
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BudgetRepository : JpaRepository<Budget, UUID> {

    fun findByUserIdAndYearAndMonth(
        userId: UUID,
        year: Int,
        month: Int
    ): List<Budget>

    fun findByUserIdAndCategoryIdAndYearAndMonth(
        userId: UUID,
        categoryId: UUID,
        year: Int,
        month: Int
    ): Budget?

}
