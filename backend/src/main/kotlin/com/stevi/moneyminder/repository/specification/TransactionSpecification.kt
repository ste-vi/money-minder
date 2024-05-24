package com.stevi.moneyminder.repository.specification

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.Category
import com.stevi.moneyminder.entity.Space
import com.stevi.moneyminder.entity.Transaction
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import java.time.LocalDateTime
import java.util.*
import org.springframework.data.jpa.domain.Specification

class TransactionSpecification(
    private val fromAccountId: UUID? = null,
    private val categoryId: UUID? = null,
    private val dateFrom: LocalDateTime? = null,
    private val dateTo: LocalDateTime? = null,
    private val spaceId: UUID
) : Specification<Transaction> {

    /**
     * Converts the given specification into a JPA CriteriaQuery.
     *
     * @param root the root of the query
     * @param query the query
     * @param cb the criteria builder
     * @return the predicate for the query
     */
    override fun toPredicate(
        root: Root<Transaction>,
        query: CriteriaQuery<*>,
        cb: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()

        categoryId?.let { categoryId ->
            predicates.add(cb.equal(root.get<Category>("category").get<UUID>("id"), categoryId))
        }

        dateFrom?.let { dateFrom ->
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), dateFrom))
        }

        dateTo?.let { dateTo ->
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), dateTo))
        }

        val fromAccountRoot = root.join<Transaction, Account>("fromAccount", JoinType.INNER)
        fromAccountId?.let { fromAccountId ->
            predicates.add(cb.equal(fromAccountRoot.get<UUID>("id"), fromAccountId))
        } ?: run {
            predicates.add(cb.equal(fromAccountRoot.get<Space>("space").get<UUID>("id"), spaceId))
        }

        return predicates.takeIf { it.isNotEmpty() }?.let { cb.and(*it.toTypedArray()) }
    }
}