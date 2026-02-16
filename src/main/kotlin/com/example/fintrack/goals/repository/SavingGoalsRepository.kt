package com.example.fintrack.goals.repository

import com.example.fintrack.goals.entity.SavingsGoal
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SavingsGoalsRepository : JpaRepository<SavingsGoal, UUID> {

    fun findAllByUserId(userId: UUID): List<SavingsGoal>

    fun findByIdAndUserId(id: UUID, userId: UUID): SavingsGoal?
}
