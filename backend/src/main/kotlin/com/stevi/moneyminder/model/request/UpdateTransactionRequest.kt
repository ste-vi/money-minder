package com.stevi.moneyminder.model.request

import com.stevi.moneyminder.entity.TransactionType
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class UpdateTransactionRequest(
    val name: String,
    val notes: String?,
    val amount: BigDecimal?,
    val date: LocalDateTime,
    val type: TransactionType,
    val categoryId: UUID?,
    val fromAccountId: UUID?,
    val toAccountId: UUID?
) : Serializable