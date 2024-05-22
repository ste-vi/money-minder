package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Space
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface SpaceRepository : JpaRepository<Space, UUID> {
}