package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.response.BankResponse
import com.stevi.moneyminder.service.BankService
import com.stevi.moneyminder.util.SecurityUtil
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/banks")
class BankController(private val bankService: BankService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getConnectedBanks(): List<BankResponse> {
        return bankService.getBanks(SecurityUtil.getCurrentUserSpaceId())
    }
}
