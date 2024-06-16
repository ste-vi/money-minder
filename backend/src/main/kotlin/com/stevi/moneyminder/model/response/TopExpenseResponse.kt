package com.stevi.moneyminder.model.response

import java.io.Serializable
import java.math.BigDecimal

data class TopExpenseResponse(
    val total: BigDecimal,
    val category: CategoryResponse,
    val currencySign: String
) : Serializable