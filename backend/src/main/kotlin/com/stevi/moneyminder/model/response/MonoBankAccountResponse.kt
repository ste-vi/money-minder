package com.stevi.moneyminder.model.response

import java.math.BigDecimal

data class MonoBankAccountResponse(
    val id: String,
    val type: String,
    val balance: BigDecimal,
    val currencyCode: Int,
    val maskedPan: String?,
    val iban: String
)