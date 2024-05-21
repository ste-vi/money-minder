package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.response.MonoBankAccountResponse
import com.stevi.moneyminder.service.MonoBankService
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mono")
class MonoBankController(var monoBankService: MonoBankService) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/link")
    fun linkClient(@RequestParam clientToken: String) {
        monoBankService.linkClient(UUID.fromString("3886a2f6-9f96-4d7e-bccc-c12cf877a4d6"), clientToken)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accounts")
    fun fetchAccounts(): List<MonoBankAccountResponse> {
        return monoBankService.fetchAccounts(UUID.fromString("3886a2f6-9f96-4d7e-bccc-c12cf877a4d6"))
    }
}
