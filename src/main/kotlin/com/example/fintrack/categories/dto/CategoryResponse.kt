package com.example.fintrack.categories.dto

import com.example.fintrack.categories.entity.CategoryType
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val type: CategoryType,
    val children: List<CategoryResponse> = emptyList()
)