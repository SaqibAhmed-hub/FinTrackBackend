package com.example.fintrack.categories.repository

import com.example.fintrack.categories.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*


interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findByUserIdAndIsDeletedFalse(userId: UUID): List<Category>

    fun findByIdAndUserIdAndIsDeletedFalse(id: UUID, userId: UUID): Category?

    fun existsByParentIdAndIsDeletedFalse(parentId: UUID): Boolean

    fun findByUserIdAndUpdatedAtAfter(userId: UUID?, lastSync: LocalDateTime?): List<Category>
}