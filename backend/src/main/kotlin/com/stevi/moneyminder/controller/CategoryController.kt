package com.stevi.moneyminder.controller

import com.stevi.moneyminder.entity.CategoryType
import com.stevi.moneyminder.model.request.CategoryRequest
import com.stevi.moneyminder.model.response.CategoryResponse
import com.stevi.moneyminder.model.response.TopExpenseResponse
import com.stevi.moneyminder.service.CategoryService
import com.stevi.moneyminder.util.SecurityUtil
import java.time.LocalDateTime
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryService: CategoryService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getAllCategories(@RequestParam(required = false) type: CategoryType?): List<CategoryResponse> {
        return categoryService.getAllCategoriesForSpace(SecurityUtil.getCurrentUserSpaceId(), type)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    fun createCategory(@RequestBody categoryRequest: CategoryRequest): CategoryResponse {
        return categoryService.createCategory(SecurityUtil.getCurrentUserSpaceId(), categoryRequest)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/top-expenses")
    fun getTopExpenses(
        @RequestParam(required = false) accountId: UUID?,
        @RequestParam(required = false) categoryIds: List<UUID>?,
        @RequestParam(required = false) needReview: Boolean?,
        @RequestParam(required = false) categoryType: CategoryType?,
        @RequestParam(required = false) categoryIdsToExclude: Set<UUID> = emptySet(),
        @RequestParam dateFrom: LocalDateTime,
        @RequestParam dateTo: LocalDateTime
    ): List<TopExpenseResponse> {
        return categoryService.getTopExpensesByCategories(
            SecurityUtil.getCurrentUserSpaceId(),
            dateFrom,
            dateTo,
            categoryType,
            categoryIdsToExclude
        )
    }
}
