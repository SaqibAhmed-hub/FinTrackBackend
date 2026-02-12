package com.example.fintrack.categories.dto

import com.example.fintrack.categories.entity.CategoryType
import java.util.UUID

data class CreateCategoryRequest(
    val name: String,
    val type: CategoryType,
    val parentId: UUID? = null
)