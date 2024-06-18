package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.Space
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.request.SpaceRequest
import com.stevi.moneyminder.model.response.SpaceResponse
import com.stevi.moneyminder.repository.SpaceRepository
import com.stevi.moneyminder.repository.UserRepository
import com.stevi.moneyminder.security.TokenService
import com.stevi.moneyminder.util.SecurityUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.LocalDateTime
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpaceService(
    private val spaceRepository: SpaceRepository,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val categoryService: CategoryService
) {

    @Transactional(readOnly = true)
    fun getSpaceResponse(spaceId: UUID): SpaceResponse {
        return spaceRepository.findById(spaceId).map { it.mapToResponse() }.orElseThrow()
    }

    @Transactional(readOnly = true)
    fun getSpaceResponses(userId: UUID): List<SpaceResponse> {
        return spaceRepository.findAllByUserIdOrderByCreatedDate(userId).map { it.mapToResponse() }
    }

    @Transactional
    fun createSpace(userId: UUID, spaceRequest: SpaceRequest): SpaceResponse {
        val user = userRepository.findById(userId).orElseThrow()

        val space = Space(
            id = null,
            name = spaceRequest.name,
            primaryCurrency = Currency.fromCode(spaceRequest.primaryCurrencyCode),
            user = user,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        val savedSpace = spaceRepository.save(space)

        categoryService.initDefaultCategories(space)

        return savedSpace.mapToResponse()
    }

    @Transactional
    fun updateSpaceName(spaceId: UUID, newName: String) {
        val space = spaceRepository.findById(spaceId).orElseThrow()
        space.name = newName
    }

    fun switchSpace(
        userId: UUID,
        spaceId: UUID,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): SpaceResponse {
        val space = spaceRepository.findByIdAndUserId(spaceId, userId) ?: throw Exception("Space not found")

        val user = userRepository.findById(userId).orElseThrow()
        user.lastLoggedInSpaceId = space.id

        val token = tokenService.generate(userId, spaceId)
        response.addHeader("Token", token)

        SecurityUtil.updateContext(userId, spaceId, request)

        return space.mapToResponse()
    }
}
