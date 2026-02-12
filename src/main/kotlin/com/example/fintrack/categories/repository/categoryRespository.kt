package com.example.fintrack.categories.repository

import com.example.fintrack.categories.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findByUserIdAndIsDeletedFalse(userId: UUID): List<Category>

    fun findByIdAndUserIdAndIsDeletedFalse(id: UUID, userId: UUID): Category?

    fun existsByParentIdAndIsDeletedFalse(parentId: UUID): Boolean
}