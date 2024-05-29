package com.stevi.moneyminder.model.request

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class UpdateTransactionRequest(
    val name: String,
    val notes: String?,
    val amount: BigDecimal?,
    val date: LocalDateTime,
    val categoryId: UUID?,
) : Serializable