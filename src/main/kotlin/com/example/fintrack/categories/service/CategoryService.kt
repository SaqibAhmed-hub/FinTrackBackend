package com.example.fintrack.categories.service

import com.example.fintrack.categories.dto.CategoryResponse
import com.example.fintrack.categories.dto.CreateCategoryRequest
import com.example.fintrack.categories.dto.UpdateCategoryRequest
import com.example.fintrack.categories.entity.Category
import com.example.fintrack.categories.repository.CategoryRepository
import com.example.fintrack.user.service.CustomUserDetailsService
import jakarta.transaction.Transactional
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val userService: CustomUserDetailsService
) {

    @Transactional
    fun create(request: CreateCategoryRequest): CategoryResponse {
        val user = userService.getCurrentUser()

        val parent = request.parentId?.let {
            categoryRepository.findByIdAndUserIdAndIsDeletedFalse(it, user.id!!)
                ?: throw RuntimeException("Parent not found")
        }

        val category = Category(
            name = request.name,
            type = request.type,
            user = user,
            parent = parent
        )

        return categoryRepository.save(category).toResponse()
    }

    @Transactional
    fun getAll(): List<CategoryResponse> {
        val user = userService.getCurrentUser()
        val categories = categoryRepository
            .findByUserIdAndIsDeletedFalse(user.id!!)

        return buildHierarchy(categories)
    }

    @Transactional
    fun update(id: UUID, request: UpdateCategoryRequest): CategoryResponse {
        val user = userService.getCurrentUser()

        val category = categoryRepository
            .findByIdAndUserIdAndIsDeletedFalse(id, user.id!!)
            ?: throw RuntimeException("Category not found")

        category.name = request.name

        return categoryRepository.save(category).toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        val user = userService.getCurrentUser()

        val category = categoryRepository
            .findByIdAndUserIdAndIsDeletedFalse(id, user.id!!)
            ?: throw RuntimeException("Category not found")

        if (categoryRepository.existsByParentIdAndIsDeletedFalse(id)) {
            throw RuntimeException("Cannot delete category with subcategories")
        }

        category.isDeleted = true
        categoryRepository.save(category)
    }

    private fun buildHierarchy(categories: List<Category>): List<CategoryResponse> {
        val map = categories.associateBy { it.id }

        val responseMap = categories.associate {
            it.id!! to CategoryResponse(
                id = it.id!!,
                name = it.name,
                type = it.type,
                children = mutableListOf()
            )
        }.toMutableMap()

        val rootCategories = mutableListOf<CategoryResponse>()

        categories.forEach { category ->
            if (category.parent == null) {
                rootCategories.add(responseMap[category.id!!]!!)
            } else {
                val parentResponse = responseMap[category.parent!!.id!!]
                (parentResponse!!.children as MutableList)
                    .add(responseMap[category.id!!]!!)
            }
        }

        return rootCategories
    }

    private fun Category.toResponse() =
        CategoryResponse(
            id = id!!,
            name = name,
            type = type
        )
}
