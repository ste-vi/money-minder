package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.request.LinkMonoBankAccountRequest
import com.stevi.moneyminder.model.response.MonoBankAccountResponse
import com.stevi.moneyminder.service.MonoBankService
import com.stevi.moneyminder.util.SecurityUtil
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mono")
class MonoBankController(private val monoBankService: MonoBankService) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/link")
    fun linkClient(@RequestParam clientToken: String) {
        monoBankService.linkClient(SecurityUtil.getCurrentUserSpaceId(), clientToken)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accounts")
    fun fetchAccounts(): List<MonoBankAccountResponse> {
        return monoBankService.fetchAccounts(SecurityUtil.getCurrentUserSpaceId())
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/accounts/link")
    fun linkAccount(@RequestBody monoBankAccountRequest: LinkMonoBankAccountRequest) {
        monoBankService.linkAccount(SecurityUtil.getCurrentUserSpaceId(), monoBankAccountRequest)
    }
}
