package com.stevi.moneyminder.repository.projection

import com.stevi.moneyminder.entity.Account

interface AccountMonoBankTokenProjection {

    fun getMonoBankToken(): String
    fun getAccount(): Account
}