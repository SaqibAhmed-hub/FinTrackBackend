package com.example.fintrack.goals.service

import com.example.fintrack.goals.dto.AddContributionRequest
import com.example.fintrack.goals.dto.CreateSavingsGoalRequest
import com.example.fintrack.goals.dto.SavingsGoalResponse
import com.example.fintrack.goals.dto.UpdateSavingsGoalRequest
import com.example.fintrack.goals.entity.SavingsGoal
import com.example.fintrack.goals.repository.SavingsGoalsRepository
import com.example.fintrack.user.service.CustomUserDetailsService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

@Service
class SavingsGoalService(
    private val savingsGoalRepository: SavingsGoalsRepository,
    private val userService: CustomUserDetailsService
) {

    fun createGoal(request: CreateSavingsGoalRequest): SavingsGoalResponse {

        val user = userService.getCurrentUser()

        val goal = SavingsGoal(
            name = request.name,
            targetAmount = request.targetAmount,
            targetDate = request.targetDate,
            user = user
        )

        return mapToResponse(savingsGoalRepository.save(goal))
    }

    fun updateGoal(id: UUID, request: UpdateSavingsGoalRequest): SavingsGoalResponse {

        val user = userService.getCurrentUser()

        val goal = savingsGoalRepository.findByIdAndUserId(id, user.id!!)
            ?: throw RuntimeException("Goal not found")

        if (goal.completed) {
            throw RuntimeException("Cannot update completed goal")
        }

        goal.name = request.name
        goal.targetAmount = request.targetAmount
        goal.targetDate = request.targetDate

        if (goal.savedAmount >= goal.targetAmount) {
            goal.completed = true
        }

        return mapToResponse(savingsGoalRepository.save(goal))
    }


    fun addContribution(id: UUID, request: AddContributionRequest): SavingsGoalResponse {

        val user = userService.getCurrentUser()

        val goal = savingsGoalRepository.findByIdAndUserId(id, user.id!!)
            ?: throw RuntimeException("Goal not found")

        if (goal.completed) {
            throw RuntimeException("Goal already completed")
        }

        if (request.amount <= BigDecimal.ZERO) {
            throw RuntimeException("Contribution must be positive")
        }

        goal.savedAmount = goal.savedAmount.add(request.amount)

        if (goal.savedAmount >= goal.targetAmount) {
            goal.completed = true
        }


        return mapToResponse(savingsGoalRepository.save(goal))
    }

    fun getMyGoals(): List<SavingsGoalResponse> {

        val user = userService.getCurrentUser()

        return savingsGoalRepository
            .findAllByUserIdAndIsDeletedFalse(user.id!!)
            .map { mapToResponse(it) }
    }

    fun deleteGoal(id: UUID) {

        val user = userService.getCurrentUser()

        val goal = savingsGoalRepository.findByIdAndUserId(id, user.id!!)
            ?: throw RuntimeException("Goal not found")

        savingsGoalRepository.delete(goal)
    }

    fun getGoalSummary(): Map<String, Any> {

        val user = userService.getCurrentUser()

        val goals = savingsGoalRepository.findAllByUserIdAndIsDeletedFalse(user.id!!)

        val totalTarget = goals.sumOf { it.targetAmount }
        val totalSaved = goals.sumOf { it.savedAmount }

        return mapOf(
            "totalGoals" to goals.size,
            "totalTargetAmount" to totalTarget,
            "totalSavedAmount" to totalSaved,
            "remainingAmount" to totalTarget.subtract(totalSaved)
        )
    }



    private fun mapToResponse(goal: SavingsGoal): SavingsGoalResponse {

        val remaining = goal.targetAmount.subtract(goal.savedAmount)
        val progress = if (goal.targetAmount > BigDecimal.ZERO)
            goal.savedAmount
                .divide(goal.targetAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .toDouble()
        else 0.0

        val today = LocalDate.now()

        val status = when {
            goal.completed -> "COMPLETED"
            today.isAfter(goal.targetDate) -> "OVERDUE"
            else -> "ACTIVE"
        }

        return SavingsGoalResponse(
            id = goal.id!!,
            name = goal.name,
            targetAmount = goal.targetAmount,
            savedAmount = goal.savedAmount,
            remainingAmount = remaining.coerceAtLeast(BigDecimal.ZERO),
            progressPercentage = progress,
            targetDate = goal.targetDate,
            completed = goal.completed,
            status = status
        )
    }
}
