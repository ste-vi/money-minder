package com.stevi.moneyminder.model.request

import com.stevi.moneyminder.entity.CategoryType
import java.io.Serializable

data class CategoryRequestDefault(
    val name: String,
    val icon: String,
    val position: Int,
    val type: CategoryType,
    val subCategories: List<CategoryRequestDefault> = emptyList()
) : Serializable {

    constructor() : this("", "", 0, CategoryType.EXPENSE, emptyList())
}