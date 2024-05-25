package com.stevi.moneyminder.model.response

import com.stevi.moneyminder.entity.ConditionType
import java.io.Serializable
import java.util.*

data class RuleResponse(
    val id: UUID,
    val condition: ConditionResponse? = null,
    val assignCategoryId: UUID? = null
) : Serializable {

    data class ConditionResponse(
        val id: UUID? = null,
        val textToApply: String? = null,
        val type: ConditionType? = null
    ) : Serializable
}