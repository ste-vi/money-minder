package com.stevi.moneyminder.controller

import com.stevi.moneyminder.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UserController(val userService: UserService) {

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    fun login() {
        userService.login();
    }
}