package com.stevi.moneyminder.model.request

import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.entity.Currency
import java.io.Serializable
import java.math.BigDecimal

data class AccountRequest(
    val name: String,
    val balance: BigDecimal?,
    val currency: Currency,
    val type: AccountType
) : Serializable