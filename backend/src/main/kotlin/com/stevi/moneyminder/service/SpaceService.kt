package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.response.SpaceResponse
import com.stevi.moneyminder.repository.SpaceRepository
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpaceService(
    private val spaceRepository: SpaceRepository
) {

    @Transactional(readOnly = true)
    fun getSpaceResponse(spaceId: UUID): SpaceResponse {
        return spaceRepository.findById(spaceId).map { it.mapToResponse() }.orElseThrow()
    }

}
