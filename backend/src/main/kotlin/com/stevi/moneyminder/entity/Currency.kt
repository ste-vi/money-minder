package com.stevi.moneyminder.entity

enum class Currency(val code: Int, val fullName: String) {
    UAH(980, "Hryvnia"),
    USD(840, "United States Dollar"),
    EUR(978, "Euro");

    companion object {
        fun fromCode(code: Int): Currency {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Currency with code $code not found")
        }
    }
}