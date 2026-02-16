package com.example.fintrack.transaction.repository

import com.example.fintrack.transaction.entity.Transaction
import com.example.fintrack.transaction.entity.TransactionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

interface TransactionRepository : JpaRepository<Transaction, UUID> {

    fun findAllByUserIdAndDeletedFalse(userId: UUID): List<Transaction>

    fun findByIdAndUserIdAndDeletedFalse(id: UUID, userId: UUID): Transaction?

    fun findAllByUserIdAndDeletedFalseAndCreatedAtBetween(
        userId: UUID,
        start: LocalDateTime,
        end: LocalDateTime,
        pageable: Pageable
    ): Page<Transaction>

    @Query("""
    SELECT SUM(t.amount) FROM Transaction t
    WHERE t.user.id = :userId
    AND t.type = :type
    AND t.deleted = false
    AND t.createdAt BETWEEN :start AND :end
""")
    fun sumByTypeAndMonth(
        userId: UUID,
        type: TransactionType,
        start: LocalDateTime,
        end: LocalDateTime
    ): BigDecimal?


    @Query("""
    SELECT SUM(t.amount) FROM Transaction t
    WHERE t.user.id = :userId
    AND t.type = :type
    AND t.deleted = false
""")
    fun sumByType(userId: UUID, type: TransactionType): BigDecimal?

    @Query("""
    SELECT SUM(t.amount)
    FROM Transaction t
    WHERE t.user.id = :userId
    AND t.category.id = :categoryId
    AND t.type = 'EXPENSE'
    AND t.deleted = false
    AND t.createdAt BETWEEN :start AND :end
""")
    fun sumExpenseByCategoryAndMonth(
        userId: UUID,
        categoryId: UUID,
        start: LocalDateTime,
        end: LocalDateTime
    ): BigDecimal?
}
