package com.stevi.moneyminder.controller

import com.stevi.moneyminder.model.request.SpaceRequest
import com.stevi.moneyminder.model.response.SpaceResponse
import com.stevi.moneyminder.service.SpaceService
import com.stevi.moneyminder.service.UserService
import com.stevi.moneyminder.util.SecurityUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/spaces")
class SpaceController(private val spaceService: SpaceService) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    fun getCurrentSpace(): SpaceResponse {
        return spaceService.getSpaceResponse(SecurityUtil.getCurrentUserSpaceId());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/switch/{spaceId}")
    fun switchSpace(
        @PathVariable spaceId: UUID,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): SpaceResponse {
        return spaceService.switchSpace(SecurityUtil.getCurrentUserId(), spaceId, request, response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getSpaces(): List<SpaceResponse> {
        return spaceService.getSpaceResponses(SecurityUtil.getCurrentUserId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createSpace(spaceRequest: SpaceRequest): SpaceResponse {
        return spaceService.createSpace(SecurityUtil.getCurrentUserId(), spaceRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{spaceId}")
    fun updateSpaceName(@PathVariable spaceId: UUID, newName: String) {
        spaceService.updateSpaceName(spaceId, newName);
    }
}