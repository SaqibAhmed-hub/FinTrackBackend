package com.example.fintrack.debt.repository

import com.example.fintrack.debt.entity.Debt
import com.example.fintrack.debt.entity.DebtPayment
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.UUID

interface DebtRepository : JpaRepository<Debt, UUID> {
    fun findAllByUserIdAndIsDeletedFalse(userId: UUID): List<Debt>
    fun findByIdAndUserId(id: UUID, userId: UUID): Debt?

    fun findByUserIdAndUpdatedAtAfter(
        userId: UUID,
        updatedAt: LocalDateTime
    ): List<Debt>
}


interface DebtPaymentRepository : JpaRepository<DebtPayment, UUID> {
    fun findAllByDebtId(debtId: UUID): List<DebtPayment>
}
