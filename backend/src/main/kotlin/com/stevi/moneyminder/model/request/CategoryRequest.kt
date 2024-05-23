package com.stevi.moneyminder.model.request

import com.stevi.moneyminder.entity.CategoryType
import java.io.Serializable

data class CategoryRequest(
    val name: String,
    val icon: String,
    val position: Int,
    val type: CategoryType
) : Serializable