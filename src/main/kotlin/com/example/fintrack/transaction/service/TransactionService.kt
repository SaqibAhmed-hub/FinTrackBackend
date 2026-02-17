package com.example.fintrack.transaction.service

import com.example.fintrack.categories.repository.CategoryRepository
import com.example.fintrack.transaction.dto.CreateTransactionRequest
import com.example.fintrack.transaction.dto.TransactionResponse
import com.example.fintrack.transaction.dto.UpdateTransactionRequest
import com.example.fintrack.transaction.entity.PaymentMethod
import com.example.fintrack.transaction.entity.Transaction
import com.example.fintrack.transaction.entity.TransactionType
import com.example.fintrack.transaction.repository.TransactionRepository
import com.example.fintrack.user.service.CustomUserDetailsService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userService: CustomUserDetailsService
) {

    fun createTransaction(request: CreateTransactionRequest): TransactionResponse {

        val user = userService.getCurrentUser()

        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { RuntimeException("Category not found") }

        val transaction = Transaction(
            amount = request.amount,
            description = request.description,
            type = request.type,
            category = category,
            user = user
        )

        val saved = transactionRepository.save(transaction)

        return saved.toResponse()
    }


    fun getMyTransactions(): List<TransactionResponse>{
        val user = userService.getCurrentUser()
        return transactionRepository.findAllByUserIdAndDeletedFalse(user.id!!).map {
            it.toResponse()
        }
    }

    fun getTransactions(
        startDate: LocalDate?,
        endDate: LocalDate?,
        pageable: Pageable
    ): Page<TransactionResponse> {

        val user = userService.getCurrentUser()

        val start = startDate?.atStartOfDay()
            ?: LocalDateTime.of(2000, 1, 1, 0, 0)

        val end = endDate?.atTime(23, 59, 59)
            ?: LocalDateTime.now()

        return transactionRepository
            .findAllByUserIdAndDeletedFalseAndCreatedAtBetween(
                user.id!!,
                start,
                end,
                pageable
            )
            .map { it.toResponse() }
    }


    fun deleteTransaction(id: UUID) {

        val user = userService.getCurrentUser()

        val transaction = transactionRepository
            .findByIdAndUserIdAndDeletedFalse(id, user.id!!)
            ?: throw RuntimeException("Transaction not found")

        transaction.deleted = true
        transactionRepository.save(transaction)
    }

    fun updateTransaction(id: UUID, request: UpdateTransactionRequest): TransactionResponse {

        val user = userService.getCurrentUser()

        val transaction = transactionRepository
            .findByIdAndUserIdAndDeletedFalse(id, user.id!!)
            ?: throw RuntimeException("Transaction not found")

        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { RuntimeException("Category not found") }

        transaction.amount = request.amount
        transaction.description = request.description
        transaction.type = request.type
        transaction.category = category

        return transactionRepository.save(transaction).toResponse()
    }

    fun getMonthlySummary(year: Int, month: Int): Map<String, BigDecimal> {

        val user = userService.getCurrentUser()

        val start = LocalDate.of(year, month, 1).atStartOfDay()
        val end = start.plusMonths(1).minusSeconds(1)

        val income = transactionRepository.sumByTypeAndMonth(
            user.id!!, TransactionType.INCOME, start, end
        ) ?: BigDecimal.ZERO

        val expense = transactionRepository.sumByTypeAndMonth(
            user.id!!, TransactionType.EXPENSE, start, end
        ) ?: BigDecimal.ZERO

        return mapOf(
            "income" to income,
            "expense" to expense,
            "balance" to income.subtract(expense)
        )
    }

    fun getDashboardTotals(): Map<String, BigDecimal> {

        val user = userService.getCurrentUser()

        val income = transactionRepository.sumByType(user.id!!, TransactionType.INCOME)
            ?: BigDecimal.ZERO

        val expense = transactionRepository.sumByType(user.id!!, TransactionType.EXPENSE)
            ?: BigDecimal.ZERO

        return mapOf(
            "totalIncome" to income,
            "totalExpense" to expense,
            "balance" to income.subtract(expense)
        )
    }


    fun getMonthlyPaymentMethod(paymentMethod: PaymentMethod,year: Int,month: Int): Map<String, String> {
        val user = userService.getCurrentUser()
        val start = LocalDate.of(year, month, 1).atStartOfDay()
        val end = start.plusMonths(1).minusSeconds(1)
        val payment =  transactionRepository.getMonthlySummaryByPaymentMethod(user.id!!,paymentMethod,start,end) ?: BigDecimal.ZERO
        return mapOf(
            "paymentMethod" to paymentMethod.name,
            "amount" to payment.toString()
        )
    }


    private fun Transaction.toResponse() = TransactionResponse(
        id = id!!,
        amount = amount,
        description = description,
        type = type,
        paymentMethod = paymentMethod,
        categoryName = category.name,
        createdAt = createdAt
    )
}
