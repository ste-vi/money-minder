package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Rule
import com.stevi.moneyminder.entity.Transaction
import com.stevi.moneyminder.entity.TransactionType
import com.stevi.moneyminder.entity.applyRule
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.request.CreateTransactionRequest
import com.stevi.moneyminder.model.request.TransactionSearchRequest
import com.stevi.moneyminder.model.request.UpdateTransactionRequest
import com.stevi.moneyminder.model.response.PageResponse
import com.stevi.moneyminder.model.response.TransactionResponse
import com.stevi.moneyminder.repository.CategoryRepository
import com.stevi.moneyminder.repository.RuleRepository
import com.stevi.moneyminder.repository.TransactionRepository
import com.stevi.moneyminder.repository.specification.TransactionRuleSpecification
import com.stevi.moneyminder.repository.specification.TransactionSearchSpecification
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountService: AccountService,
    private val ruleRepository: RuleRepository
) {

    @Transactional(readOnly = true)
    fun searchTransactions(spaceId: UUID, searchRequest: TransactionSearchRequest): PageResponse<TransactionResponse> {
        val specification = TransactionSearchSpecification(
            searchRequest.name,
            searchRequest.notes,
            searchRequest.fromAccountId,
            searchRequest.categoryId,
            searchRequest.needReview,
            searchRequest.dateFrom,
            searchRequest.dateTo,
            spaceId
        )
        // todo: search transfer?

        val pageable = PageRequest.of(
            searchRequest.page ?: 0,
            searchRequest.size ?: 5,
            Sort.by("createdDate").descending()
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
        val rules = ruleRepository.findAllBySpaceIdOrderByConditionTextToApplyAsc(currentUserSpaceId)

        fromAccount.monoBankId?.let {
            throw IllegalArgumentException("Account is linked to Monobank, manual transaction is not allowed")
        }

        val category = request.categoryId?.let {
            categoryRepository.findById(it).orElseThrow { IllegalArgumentException("Category not found") }
        }

        val transaction = Transaction(
            id = null,
            name = request.name,
            notes = if (request.notes.isNullOrBlank()) null else request.notes,
            amount = request.amount,
            currency = request.currency,
            date = request.date,
            monoBankId = null,
            fromAccount = fromAccount,
            toAccount = toAccount,
            category = category,
            type = request.type,
            createdDate = LocalDateTime.now()
        )

        rules.stream().forEach { rule -> transaction.applyRule(rule) }

        val savedTransaction = transactionRepository.save(transaction)

        if (request.type == TransactionType.INCOME) {
            accountService.increaseAccountBalanceByAmount(fromAccount, request.amount)
        } else if (request.type == TransactionType.EXPENSE) {
            accountService.decreaseAccountBalanceByAmount(fromAccount, request.amount)
        } else if (request.type == TransactionType.TRANSFER && toAccount != null) {
            accountService.decreaseAccountBalanceByAmount(fromAccount, request.amount)
            accountService.increaseAccountBalanceByAmount(toAccount, request.amount)
        }

        return savedTransaction.mapToResponse()
    }

    @Transactional
    fun updateTransaction(id: UUID, request: UpdateTransactionRequest) {
        val transaction = getTransactionById(id)
        transaction.name = request.name
        transaction.notes = request.notes
        transaction.date = request.date

        request.categoryId?.let {
            if (request.categoryId != transaction.category?.id) {
                transaction.category =
                    categoryRepository.findById(it).orElseThrow { IllegalArgumentException("Category not found") }
            }
        }

        request.toAccountId?.let {
            if (request.toAccountId != transaction.toAccount?.id) {
                transaction.toAccount = accountService.getAccountById(it)
            }
        }

        request.amount?.let {
            if (transaction.monoBankId == null && request.amount != transaction.amount) {
                val previousAmount = transaction.amount
                transaction.amount = it
                if (transaction.type == TransactionType.INCOME) {
                    accountService.updateAccountBalance(transaction.fromAccount, previousAmount, it)
                } else if (transaction.type == TransactionType.EXPENSE) {
                    accountService.updateAccountBalance(
                        transaction.fromAccount,
                        previousAmount.multiply(BigDecimal.valueOf(-1)),
                        it.multiply(BigDecimal.valueOf(-1))
                    )
                } else if (transaction.type == TransactionType.TRANSFER && request.toAccountId != null) {
                    accountService.updateAccountBalance(
                        transaction.fromAccount,
                        previousAmount.multiply(BigDecimal.valueOf(-1)),
                        it.multiply(BigDecimal.valueOf(-1))
                    )
                    accountService.updateAccountBalance(transaction.toAccount!!, previousAmount, it)
                }
            }
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

        if (transaction.type == TransactionType.INCOME) {
            accountService.decreaseAccountBalanceByAmount(transaction.fromAccount, transaction.amount)
        } else if (transaction.type == TransactionType.EXPENSE) {
            accountService.increaseAccountBalanceByAmount(transaction.fromAccount, transaction.amount)
        } else if (transaction.type == TransactionType.TRANSFER && transaction.toAccount != null) {
            accountService.increaseAccountBalanceByAmount(transaction.fromAccount, transaction.amount)
            accountService.decreaseAccountBalanceByAmount(transaction.toAccount!!, transaction.amount)
        }
    }

    private fun getTransactionById(id: UUID): Transaction {
        return transactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Transaction not found") }
    }

    @Transactional
    fun applyRuleToExistingTransactions(spaceId: UUID, rule: Rule) {
        val ruleSpecification = TransactionRuleSpecification(rule, spaceId)
        val transactions = transactionRepository.findAll(ruleSpecification)

        transactions.map { t -> t.applyRule(rule) }

        transactionRepository.saveAll(transactions)
    }

}
