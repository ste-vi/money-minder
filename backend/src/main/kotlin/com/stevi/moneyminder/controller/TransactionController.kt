package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.response.TransactionResponse
import com.stevi.moneyminder.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
class TransactionController(var transactionService: TransactionService) {

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/search")
    fun searchTransactions(): List<TransactionResponse> {
        return transactionService.searchTransactions();
    }
}
