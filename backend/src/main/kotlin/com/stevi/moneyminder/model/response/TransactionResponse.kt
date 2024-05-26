package com.stevi.moneyminder.model.response

import com.stevi.moneyminder.entity.Currency
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val name: String,
    val notes: String? = null,
    val amount: BigDecimal = BigDecimal.ZERO,
    val currency: CurrencyResponse,
    val fromAccountId: UUID,
    val toAccountId: UUID?,
    val date: LocalDateTime,
    val categoryId: UUID?
) : Serializable