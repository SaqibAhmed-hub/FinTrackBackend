package com.example.fintrack.budget.controller

import com.example.fintrack.budget.dto.BudgetResponse
import com.example.fintrack.budget.dto.CreateBudgetRequest
import com.example.fintrack.budget.dto.UpdateBudgetRequest
import com.example.fintrack.budget.service.BudgetService
import com.example.fintrack.user.dto.CustomUserDetails
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Budgets")
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

    @PutMapping("/{id}")
    fun updateBudget(
        @PathVariable id: UUID,
        @RequestBody request: UpdateBudgetRequest,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<BudgetResponse> {

        val updated = budgetService.updateBudget(
            userId = user.id,
            budgetId = id,
            request = request
        )

        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteBudget(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<Void> {

        budgetService.deleteBudget(user.id, id)

        return ResponseEntity.noContent().build()
    }

}
