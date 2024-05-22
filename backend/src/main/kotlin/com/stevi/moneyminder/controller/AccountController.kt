package com.stevi.moneyminder.controller

import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.AccountType
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountController {

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/types")
    fun getTypes(): List<AccountType> {
        return AccountType.entries.toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/currencies")
    fun getCurrencies(): List<Currency> {
        return Currency.entries.toList();
    }
}
