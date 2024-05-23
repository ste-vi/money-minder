package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Category
import com.stevi.moneyminder.model.request.CategoryRequest
import com.stevi.moneyminder.repository.CategoryRepository
import com.stevi.moneyminder.model.response.CategoryResponse
import com.stevi.moneyminder.repository.SpaceRepository
import java.util.UUID
import java.util.stream.Collectors
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(val categoryRepository: CategoryRepository, val spaceRepository: SpaceRepository) {

    @Transactional(readOnly = true)
    fun getAllCategoriesForSpace(spaceId: UUID): List<CategoryResponse> {
        return categoryRepository.findAllBySpaceIdOrderByPosition(spaceId)
            .stream()
            .map { category ->
                mapToResponse(category)
            }
            .collect(Collectors.toList())
    }

    @Transactional
    fun createCategory(spaceId: UUID, categoryRequest: CategoryRequest): CategoryResponse {
        val space = spaceRepository.findById(spaceId).orElseThrow()
        val category = Category(
            null,
            categoryRequest.name,
            categoryRequest.icon,
            categoryRequest.position,
            categoryRequest.type,
            space
        )
        val savedCategory = categoryRepository.save(category)
        return mapToResponse(savedCategory)
    }

    private fun mapToResponse(category: Category) = CategoryResponse(
        category.id,
        category.name,
        category.icon,
        category.position,
        category.type
    )

}
