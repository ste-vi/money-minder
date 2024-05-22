package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.MonoBankInfo
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface MonoBankInfoRepository : JpaRepository<MonoBankInfo, UUID> {

    fun findBySpaceId(spaceId: UUID): Optional<MonoBankInfo>
}