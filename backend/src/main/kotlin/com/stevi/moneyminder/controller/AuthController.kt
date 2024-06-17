package com.stevi.moneyminder.controller

import com.stevi.moneyminder.properties.GoogleProperties
import com.stevi.moneyminder.security.GoogleAuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping
class AuthController(
    private val googleAuthService: GoogleAuthService,
    @Value("\${client.host}") private val clientHost: String
) {

    @GetMapping("/login")
    fun login(response: HttpServletResponse) {
        response.sendRedirect(googleAuthService.getUri());
    }

    @GetMapping("/google/oauth2callback")
    @ResponseStatus(HttpStatus.OK)
    fun googleCallback(request: HttpServletRequest, response: HttpServletResponse) {
        val fullUrlBuffer = request.requestURL
        if (request.queryString != null) {
            fullUrlBuffer.append('?').append(request.queryString)
        }

        val authResponse = googleAuthService.findAndAuthenticateUser(fullUrlBuffer)

        response.sendRedirect(
            clientHost + "/auth?accessToken=" + authResponse.token
        )
    }

}