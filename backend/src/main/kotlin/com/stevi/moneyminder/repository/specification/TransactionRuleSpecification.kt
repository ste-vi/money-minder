package com.stevi.moneyminder.repository.specification

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.ConditionType
import com.stevi.moneyminder.entity.Rule
import com.stevi.moneyminder.entity.Space
import com.stevi.moneyminder.entity.Transaction
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import java.util.*
import org.springframework.data.jpa.domain.Specification

class TransactionRuleSpecification(
    private val rule: Rule,
    private val spaceId: UUID
) : Specification<Transaction> {

    override fun toPredicate(
        root: Root<Transaction>,
        query: CriteriaQuery<*>,
        cb: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()

        if (rule.condition.type == ConditionType.TEXT_CONTAINS) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%${rule.condition.textToApply.lowercase()}%"))
        } else if (rule.condition.type == ConditionType.TEXT_EQUALS) {
            predicates.add(cb.like(cb.lower(root.get("name")), rule.condition.textToApply.lowercase()))
        }

        val fromAccountRoot = root.join<Transaction, Account>("fromAccount", JoinType.INNER)
        predicates.add(cb.equal(fromAccountRoot.get<Space>("space").get<UUID>("id"), spaceId))

        return predicates.takeIf { it.isNotEmpty() }?.let { cb.and(*it.toTypedArray()) }
    }
}