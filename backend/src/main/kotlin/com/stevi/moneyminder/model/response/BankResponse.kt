package com.stevi.moneyminder.model.response

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class BankResponse(
    val id: UUID?,
    val name: String,
    val bankType: BankType
) : Serializable {

    enum class BankType {
        MONOBANK
    }
}