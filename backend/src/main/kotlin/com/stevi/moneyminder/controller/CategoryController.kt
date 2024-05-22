package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.request.CategoryRequest
import com.stevi.moneyminder.model.response.CategoryResponse
import com.stevi.moneyminder.service.CategoryService
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController(val categoryService: CategoryService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/types")
    fun getAllCategories(): List<CategoryResponse> {
        return categoryService.getAllCategoriesForSpace(UUID.fromString("f6ced947-1e08-4d91-9549-1fbc3361905f"))
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/types")
    fun createCategory(categoryRequest: CategoryRequest): CategoryResponse {
     return categoryService.createCategory(UUID.fromString("f6ced947-1e08-4d91-9549-1fbc3361905f"), categoryRequest)
    }
}
