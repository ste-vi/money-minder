package com.stevi.moneyminder.controller

import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.request.AccountRequest
import com.stevi.moneyminder.model.response.AccountResponse
import com.stevi.moneyminder.model.response.AccountTypeResponse
import com.stevi.moneyminder.service.AccountService
import com.stevi.moneyminder.util.SecurityUtil
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(private val accountService: AccountService) {

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/types")
    fun getTypes(): List<AccountTypeResponse> {
        return AccountType
            .entries
            .map { a -> a.mapToResponse() }
            .toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getAccounts(): List<AccountResponse> {
        return accountService.getAllAccounts(SecurityUtil.getCurrentUserSpaceId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createAccount(@RequestBody accountRequest: AccountRequest): AccountResponse {
        return accountService.createAccount(SecurityUtil.getCurrentUserSpaceId(), accountRequest)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    fun createAccount(@PathVariable id: UUID, @RequestBody accountRequest: AccountRequest) {
        return accountService.updateAccount(id, accountRequest)
    }
}
