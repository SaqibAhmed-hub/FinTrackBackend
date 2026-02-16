package com.example.fintrack.goals.controller

import com.example.fintrack.goals.dto.AddContributionRequest
import com.example.fintrack.goals.dto.CreateSavingsGoalRequest
import com.example.fintrack.goals.dto.SavingsGoalResponse
import com.example.fintrack.goals.dto.UpdateSavingsGoalRequest
import com.example.fintrack.goals.service.SavingsGoalService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/savings-goals")
class SavingsGoalController(
    private val savingsGoalService: SavingsGoalService
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateSavingsGoalRequest
    ): ResponseEntity<SavingsGoalResponse> {

        return ResponseEntity.ok(savingsGoalService.createGoal(request))
    }

    @PostMapping("/{id}/contribute")
    fun contribute(
        @PathVariable id: UUID,
        @RequestBody request: AddContributionRequest
    ): ResponseEntity<SavingsGoalResponse> {

        return ResponseEntity.ok(
            savingsGoalService.addContribution(id, request)
        )
    }

    @GetMapping
    fun getMyGoals(): ResponseEntity<List<SavingsGoalResponse>> {

        return ResponseEntity.ok(
            savingsGoalService.getMyGoals()
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateSavingsGoalRequest
    ): ResponseEntity<SavingsGoalResponse> {

        return ResponseEntity.ok(
            savingsGoalService.updateGoal(id, request)
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        savingsGoalService.deleteGoal(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/summary")
    fun summary(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(savingsGoalService.getGoalSummary())
    }



}
