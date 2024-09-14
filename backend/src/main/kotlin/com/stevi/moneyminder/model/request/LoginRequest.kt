package com.stevi.moneyminder.model.request

import java.io.Serializable
import java.math.BigDecimal

data class LoginRequest(
    val username: String,
    val password: String,
) : Serializable