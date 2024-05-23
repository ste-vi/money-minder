package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.request.TransactionSearchRequest
import com.stevi.moneyminder.model.response.PageResponse
import com.stevi.moneyminder.model.response.TransactionResponse
import com.stevi.moneyminder.service.TransactionService
import com.stevi.moneyminder.util.SecurityUtil
import java.time.LocalDateTime
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
class TransactionController(var transactionService: TransactionService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    fun searchTransactions(
        @RequestParam(required = false) accountId: UUID?,
        @RequestParam(required = false) categoryId: UUID?,
        @RequestParam(required = false) dateFrom: LocalDateTime?,
        @RequestParam(required = false) dateTo: LocalDateTime?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): PageResponse<TransactionResponse> {
        val transactionSearchRequest = TransactionSearchRequest(
            accountId,
            categoryId,
            dateFrom,
            dateTo,
            page,
            size
        )
        return transactionService.searchTransactions(SecurityUtil.getCurrentUserSpaceId(), transactionSearchRequest)
    }
}
