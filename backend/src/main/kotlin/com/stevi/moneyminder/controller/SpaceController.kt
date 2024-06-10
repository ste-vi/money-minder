package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.response.SpaceResponse
import com.stevi.moneyminder.service.SpaceService
import com.stevi.moneyminder.service.UserService
import com.stevi.moneyminder.util.SecurityUtil
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/spaces")
class SpaceController(private val spaceService: SpaceService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getSpace(): SpaceResponse {
        return spaceService.getSpaceResponse(SecurityUtil.getCurrentUserSpaceId());
    }
}