package com.stevi.moneyminder.model.request

import com.stevi.moneyminder.entity.Currency
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class UpdateTransactionRequest(
    val name: String,
    val notes: String? = null,
    val currency: Currency,
    val date: LocalDateTime,
    val categoryId: UUID?,
) : Serializable