package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Condition
import com.stevi.moneyminder.entity.Rule
import com.stevi.moneyminder.repository.RuleRepository
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.request.RuleRequest
import com.stevi.moneyminder.model.response.RuleResponse
import com.stevi.moneyminder.repository.CategoryRepository
import com.stevi.moneyminder.repository.SpaceRepository
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RuleService(
    private val ruleRepository: RuleRepository,
    private val spaceRepository: SpaceRepository,
    private val transactionService: TransactionService,
    private val categoryRepository: CategoryRepository
) {

    @Transactional(readOnly = true)
    fun getAllRulesForSpace(spaceId: UUID): List<RuleResponse> {
        return ruleRepository.findAllBySpaceId(spaceId).map { it.mapToResponse() }
    }

    @Transactional
    fun createRule(spaceId: UUID, ruleRequest: RuleRequest, applyToExistingTransactions: Boolean?): RuleResponse {
        val space = spaceRepository.findById(spaceId).orElseThrow { IllegalArgumentException("Space not found") }
        val category = categoryRepository.findById(ruleRequest.assignCategoryId)
            .orElseThrow { IllegalArgumentException("Category not found") }

        val rule = Rule(
            id = null,
            assignCategory = category,
            condition = Condition(
                id = null,
                type = ruleRequest.conditionType,
                textToApply = ruleRequest.conditionText,
            ),
            space = space
        )

        applyToExistingTransactions.let { transactionService.applyRuleToExistingTransactions(spaceId, rule) }

        return ruleRepository.save(rule).mapToResponse()
    }

}