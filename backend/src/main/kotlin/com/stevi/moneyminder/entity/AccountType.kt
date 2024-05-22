package com.stevi.moneyminder.entity

enum class AccountType(val id: Int, val fullName: String) {
    BANK_ACCOUNTS(1, "Bank accounts"),
    CASH(2, "Cash"),
    STOCKS_CRYPTO(3, "Stocks & Crypto"),
    OTHER_ASSETS(4, "Other assets");

    companion object {
        fun fromId(id: Int): AccountType {
            return entries.find { it.id == id }
                ?: throw IllegalArgumentException("Type with id $id not found")
        }
    }
}