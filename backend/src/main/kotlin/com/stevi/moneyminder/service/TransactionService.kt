package com.stevi.moneyminder.service

import com.stevi.moneyminder.model.request.TransactionSearchRequest
import com.stevi.moneyminder.model.response.PageResponse
import com.stevi.moneyminder.model.response.TransactionResponse
import com.stevi.moneyminder.repository.TransactionRepository
import com.stevi.moneyminder.repository.specification.TransactionSpecification
import java.util.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class TransactionService(val transactionRepository: TransactionRepository) {

    fun searchTransactions(spaceId: UUID, searchRequest: TransactionSearchRequest): PageResponse<TransactionResponse> {
        val specification = TransactionSpecification(
            searchRequest.accountId,
            searchRequest.categoryId,
            searchRequest.dateFrom,
            searchRequest.dateTo,
            spaceId
        )

        val pageable = PageRequest.of(
            searchRequest.page ?: 0,
            searchRequest.size ?: 5,
            Sort.by("date").descending()
        )

        val transactionPage = transactionRepository.findAll(specification, pageable)

        val content = transactionPage
            .map { transaction ->
                TransactionResponse(
                    transaction.id ?: UUID.randomUUID(),
                    transaction.name,
                    transaction.notes,
                    transaction.amount,
                    transaction.currency,
                    transaction.account.id ?: UUID.randomUUID(),
                    transaction.date,
                    transaction.category?.id,
                )
            }.toList()

        return PageResponse(
            content,
            transactionPage.number,
            transactionPage.size,
            transactionPage.totalElements,
            transactionPage.totalPages,
            transactionPage.isLast
        )
    }

}
