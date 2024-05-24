package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Transaction
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.request.CreateTransactionRequest
import com.stevi.moneyminder.model.request.TransactionSearchRequest
import com.stevi.moneyminder.model.request.UpdateTransactionRequest
import com.stevi.moneyminder.model.response.PageResponse
import com.stevi.moneyminder.model.response.TransactionResponse
import com.stevi.moneyminder.repository.CategoryRepository
import com.stevi.moneyminder.repository.TransactionRepository
import com.stevi.moneyminder.repository.specification.TransactionSpecification
import java.util.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountService: AccountService
) {

    @Transactional(readOnly = true)
    fun searchTransactions(spaceId: UUID, searchRequest: TransactionSearchRequest): PageResponse<TransactionResponse> {
        val specification = TransactionSpecification(
            searchRequest.fromAccountId,
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
                transaction.mapToResponse()
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

    @Transactional
    fun createTransaction(currentUserSpaceId: UUID, request: CreateTransactionRequest): TransactionResponse {
        val fromAccount = accountService.getAccountById(request.fromAccountId)
        val toAccount = request.toAccountId?.let { accountService.getAccountById(it) }

        fromAccount.monoBankId?.let {
            throw IllegalArgumentException("Account is linked to Monobank, manual transaction is not allowed")
        }

        val category = request.categoryId?.let {
            categoryRepository.findById(it).orElseThrow { IllegalArgumentException("Category not found") }
        }

        val transaction = Transaction(
            id = null,
            name = request.name,
            notes = request.notes,
            amount = request.amount,
            currency = request.currency,
            date = request.date,
            monoBankId = null,
            fromAccount = fromAccount,
            toAccount = toAccount,
            category = category
        )

        val savedTransaction = transactionRepository.save(transaction)

        accountService.increaseAccountBalanceByAmount(fromAccount, request.amount)
        toAccount?.let { accountService.increaseAccountBalanceByAmount(it, request.amount) }

        return savedTransaction.mapToResponse()
    }

    @Transactional
    fun updateTransaction(id: UUID, request: UpdateTransactionRequest) {
        val transaction = getTransactionById(id)

        transaction.name = request.name
        transaction.notes = request.notes
        transaction.currency = request.currency
        transaction.date = request.date

        transaction.category = request.categoryId?.let {
            categoryRepository.findById(it).orElseThrow { IllegalArgumentException("Category not found") }
        }

        transactionRepository.save(transaction)
    }

    @Transactional
    fun deleteTransaction(id: UUID) {
        val transaction = getTransactionById(id)

        transaction.monoBankId?.let {
            throw IllegalArgumentException("Bank transaction cannot be deleted")
        }

        transactionRepository.delete(transaction)
    }

    private fun getTransactionById(id: UUID): Transaction {
        return transactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Transaction not found") }
    }

}
