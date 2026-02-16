package com.example.fintrack.sync.controller

import com.example.fintrack.sync.dto.SyncResponse
import com.example.fintrack.sync.service.SyncService
import com.example.fintrack.user.dto.CustomUserDetails
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Sync")
@RestController
@RequestMapping("/api/v1/sync")
class SyncController(
    private val syncService: SyncService
) {

    @GetMapping
    fun sync(
        @RequestParam lastSync: LocalDateTime,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<SyncResponse> {

        val response = syncService.sync(
            userId = user.id,
            lastSync = lastSync
        )

        return ResponseEntity.ok(response)
    }
}
