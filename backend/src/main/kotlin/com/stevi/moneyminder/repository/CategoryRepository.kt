package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Category
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, UUID> {

    fun findAllBySpaceIdOrderByOrder(spaceId: UUID): List<Category>
}