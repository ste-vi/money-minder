package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.response.UserResponse
import com.stevi.moneyminder.service.UserService
import com.stevi.moneyminder.util.SecurityUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/current")
    fun getCurrentUser(): UserResponse {
        return userService.getCurrentUserResponse(SecurityUtil.getCurrentUserId())
    }
}