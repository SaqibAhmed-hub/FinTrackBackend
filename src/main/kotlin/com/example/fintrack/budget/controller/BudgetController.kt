package com.example.fintrack.budget.controller

import com.example.fintrack.budget.dto.BudgetResponse
import com.example.fintrack.budget.dto.CreateBudgetRequest
import com.example.fintrack.budget.service.BudgetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/budgets")
class BudgetController(
    private val budgetService: BudgetService
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateBudgetRequest
    ): ResponseEntity<BudgetResponse> {

        return ResponseEntity.ok(budgetService.createBudget(request))
    }

    @GetMapping
    fun getMonthlyBudgets(
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<List<BudgetResponse>> {

        return ResponseEntity.ok(
            budgetService.getMonthlyBudgets(year, month)
        )
    }
}
