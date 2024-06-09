package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Rule
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, UUID> {
    fun findAllBySpaceIdOrderByConditionTextToApplyAsc(spaceId: UUID): List<Rule>
}