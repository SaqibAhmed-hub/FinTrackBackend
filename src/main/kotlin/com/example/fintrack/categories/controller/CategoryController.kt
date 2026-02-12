package com.example.fintrack.categories.controller

import com.example.fintrack.categories.dto.CreateCategoryRequest
import com.example.fintrack.categories.dto.UpdateCategoryRequest
import com.example.fintrack.categories.service.CategoryService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    fun create(@RequestBody request: CreateCategoryRequest) =
        categoryService.create(request)

    @GetMapping
    fun getAll() = categoryService.getAll()

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: UpdateCategoryRequest
    ) = categoryService.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        categoryService.delete(id)
}
