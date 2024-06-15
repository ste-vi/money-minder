package com.stevi.moneyminder.model.response

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class TopExpenseResponse(
    val total: BigDecimal,
    val categoryId: UUID,
    val categoryName: String
) : Serializable