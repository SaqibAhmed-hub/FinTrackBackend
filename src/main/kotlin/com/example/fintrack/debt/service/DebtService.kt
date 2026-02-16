package com.example.fintrack.debt.service

import com.example.fintrack.debt.dto.AddDebtPaymentRequest
import com.example.fintrack.debt.dto.CreateDebtRequest
import com.example.fintrack.debt.dto.DebtResponse
import com.example.fintrack.debt.entity.Debt
import com.example.fintrack.debt.entity.DebtPayment
import com.example.fintrack.debt.repository.DebtPaymentRepository
import com.example.fintrack.debt.repository.DebtRepository
import com.example.fintrack.user.service.CustomUserDetailsService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

@Service
class DebtService(
    private val debtRepository: DebtRepository,
    private val debtPaymentRepository: DebtPaymentRepository,
    private val userService: CustomUserDetailsService
) {

    fun createDebt(request: CreateDebtRequest): DebtResponse {

        val user = userService.getCurrentUser()

        val debt = Debt(
            name = request.name,
            totalAmount = request.totalAmount,
            remainingAmount = request.totalAmount,
            interestRate = request.interestRate,
            dueDate = request.dueDate,
            user = user
        )

        return mapToResponse(debtRepository.save(debt))
    }

    fun addPayment(id: UUID, request: AddDebtPaymentRequest): DebtResponse {

        val user = userService.getCurrentUser()

        val debt = debtRepository.findByIdAndUserId(id, user.id!!)
            ?: throw RuntimeException("Debt not found")

        if (debt.closed) {
            throw RuntimeException("Debt already closed")
        }

        if (request.amount <= BigDecimal.ZERO) {
            throw RuntimeException("Payment must be positive")
        }

        if (request.amount > debt.remainingAmount) {
            throw RuntimeException("Payment exceeds remaining balance")
        }

        debt.remainingAmount = debt.remainingAmount.subtract(request.amount)

        if (debt.remainingAmount == BigDecimal.ZERO) {
            debt.closed = true
        }

        debtPaymentRepository.save(
            DebtPayment(
                amount = request.amount,
                debt = debt
            )
        )

        return mapToResponse(debtRepository.save(debt))
    }

    fun getMyDebts(): List<DebtResponse> {

        val user = userService.getCurrentUser()

        return debtRepository
            .findAllByUserIdAndIsDeletedFalse(user.id!!)
            .map { mapToResponse(it) }
    }

    private fun mapToResponse(debt: Debt): DebtResponse {

        val paid = debt.totalAmount.subtract(debt.remainingAmount)

        val progress = if (debt.totalAmount > BigDecimal.ZERO)
            paid.divide(debt.totalAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .toDouble()
        else 0.0

        return DebtResponse(
            id = debt.id!!,
            name = debt.name,
            totalAmount = debt.totalAmount,
            remainingAmount = debt.remainingAmount,
            interestRate = debt.interestRate,
            dueDate = debt.dueDate,
            progressPercentage = progress,
            closed = debt.closed
        )
    }
}
