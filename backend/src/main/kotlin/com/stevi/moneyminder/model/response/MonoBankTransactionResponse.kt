package com.stevi.moneyminder.model.response

data class MonoBankTransactionResponse(
    val id: String,
    val time: Long,
    val description: String,
    val currencyCode: Int,
    val amount: Int,
    val balance: Int,
    val comment: String?
)