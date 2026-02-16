package com.example.fintrack.debt.controller

import com.example.fintrack.debt.dto.AddDebtPaymentRequest
import com.example.fintrack.debt.dto.CreateDebtRequest
import com.example.fintrack.debt.service.DebtService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Debts")
@RestController
@RequestMapping("/api/v1/debts")
class DebtController(
    private val debtService: DebtService
) {

    @PostMapping
    fun create(@RequestBody request: CreateDebtRequest)
            = ResponseEntity.ok(debtService.createDebt(request))

    @PostMapping("/{id}/payment")
    fun addPayment(
        @PathVariable id: UUID,
        @RequestBody request: AddDebtPaymentRequest
    ) = ResponseEntity.ok(debtService.addPayment(id, request))

    @GetMapping
    fun getMyDebts()
            = ResponseEntity.ok(debtService.getMyDebts())


}
