package com.stevi.moneyminder.model.response

import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.entity.Currency
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class AccountResponse(
    val id: UUID?,
    val name: String,
    val balance: BigDecimal = BigDecimal.ZERO,
    val currency: Currency,
    val type: AccountType
) : Serializable