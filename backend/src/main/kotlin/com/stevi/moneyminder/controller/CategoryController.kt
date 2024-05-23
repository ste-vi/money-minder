package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.request.CategoryRequest
import com.stevi.moneyminder.model.response.CategoryResponse
import com.stevi.moneyminder.service.CategoryService
import com.stevi.moneyminder.util.SecurityUtil
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController(val categoryService: CategoryService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getAllCategories(): List<CategoryResponse> {
        return categoryService.getAllCategoriesForSpace(SecurityUtil.getCurrentUserSpaceId())
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    fun createCategory(@RequestBody categoryRequest: CategoryRequest): CategoryResponse {
     return categoryService.createCategory(SecurityUtil.getCurrentUserSpaceId(), categoryRequest)
    }
}
