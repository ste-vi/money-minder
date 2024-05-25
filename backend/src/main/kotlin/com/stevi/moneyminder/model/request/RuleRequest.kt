package com.stevi.moneyminder.model.request

import com.stevi.moneyminder.entity.ConditionType
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class RuleRequest(
    val conditionType: ConditionType,
    val conditionText: String,
    val assignCategoryId: UUID,
) : Serializable