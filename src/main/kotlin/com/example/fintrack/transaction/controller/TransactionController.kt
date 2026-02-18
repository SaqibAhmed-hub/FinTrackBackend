package com.example.fintrack.transaction.controller

import com.example.fintrack.transaction.dto.CreateTransactionRequest
import com.example.fintrack.transaction.dto.TransactionResponse
import com.example.fintrack.transaction.dto.UpdateTransactionRequest
import com.example.fintrack.transaction.entity.PaymentMethod
import com.example.fintrack.transaction.entity.Transaction
import com.example.fintrack.transaction.service.TransactionService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Transaction")
@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    fun create(
        @RequestBody request: CreateTransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val response = transactionService.createTransaction(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getTransactions(
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        pageable: Pageable
    ): ResponseEntity<Page<TransactionResponse>> {

        return ResponseEntity.ok(
            transactionService.getTransactions(startDate, endDate, pageable)
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateTransactionRequest
    ): ResponseEntity<TransactionResponse> {

        return ResponseEntity.ok(transactionService.updateTransaction(id, request))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        transactionService.deleteTransaction(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/monthly-summary")
    fun monthlySummary(
        @RequestParam year : Int,
        @RequestParam month : Int
    ): ResponseEntity<Map<String, BigDecimal>>{
        return ResponseEntity.ok(transactionService.getMonthlySummary(year,month))
    }

    @GetMapping("/dashboard")
    fun dashboard(
    ): ResponseEntity<Map<String, BigDecimal>>{
        return ResponseEntity.ok(transactionService.getDashboardTotals())
    }

    @GetMapping("/monthly-payment-summary")
    fun paymentMethodSummary(
        @RequestParam paymentMethod: PaymentMethod,
        @RequestParam year: Int,
        @RequestParam month: Int
    ) : ResponseEntity<Map<String, String>>{
        return ResponseEntity.ok(transactionService.getMonthlyPaymentMethod(paymentMethod,year,month))
    }

}
