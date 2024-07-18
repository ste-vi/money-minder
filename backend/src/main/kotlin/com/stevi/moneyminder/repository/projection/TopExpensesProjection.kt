package com.stevi.moneyminder.repository.projection

import com.stevi.moneyminder.entity.Category
import com.stevi.moneyminder.entity.Currency
import java.math.BigDecimal

interface TopExpensesProjection {

    fun getTotal(): BigDecimal

    fun getCategory(): Category?
}