package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.Transaction
import com.stevi.moneyminder.entity.TransactionType
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.model.request.AccountRequest
import com.stevi.moneyminder.model.response.AccountResponse
import com.stevi.moneyminder.repository.AccountRepository
import com.stevi.moneyminder.repository.SpaceRepository
import com.stevi.moneyminder.repository.TransactionRepository
import com.stevi.moneyminder.repository.projection.AccountMonoBankTokenProjection
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val spaceRepository: SpaceRepository,
    private val transactionRepository: TransactionRepository
) {

    @Transactional(readOnly = true)
    fun getAccountById(id: UUID): Account {
        return accountRepository.findById(id).orElseThrow { IllegalArgumentException("Account not found") }
    }

    @Transactional
    fun saveAccount(account: Account) {
        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    fun existsBySpaceIdAndMonoBankId(spaceId: UUID, monoBankId: String): Boolean {
        return accountRepository.existsBySpaceIdAndMonoBankId(spaceId, monoBankId);
    }

    @Transactional(readOnly = true)
    fun getMonobankAccountIds(spaceId: UUID): List<String> {
        return accountRepository.findAllMonoBankIdsBySpaceId(spaceId);
    }

    @Transactional(readOnly = true)
    fun getAllMonobankAccounts(): List<AccountMonoBankTokenProjection> {
        return accountRepository.findAllByMonoBankIdIsNotNull();
    }

    @Transactional
    fun updateAccountBalance(account: Account, balance: Int) {
        account.balance = balance.toBigDecimal().divide(BigDecimal.valueOf(100))
        accountRepository.save(account)
    }

    @Transactional(readOnly = true)
    fun getAllAccounts(spaceId: UUID): List<AccountResponse> {
        return accountRepository.findAllBySpaceId(spaceId)
            .stream()
            .map { account ->
                account.mapToResponse()
            }
            .collect(Collectors.toList())
    }

    @Transactional
    fun increaseAccountBalanceByAmount(account: Account, amount: BigDecimal) {
        account.balance = account.balance.add(amount);
        accountRepository.save(account)
    }

    @Transactional
    fun createAccount(currentUserSpaceId: UUID, accountRequest: AccountRequest): AccountResponse {
        val account = Account(
            id = null,
            name = accountRequest.name,
            description = null,
            balance = accountRequest.balance ?: BigDecimal.ZERO,
            monoBankId = null,
            currency = Currency.fromCode(accountRequest.currencyCode),
            type = AccountType.fromId(accountRequest.typeId),
            space = spaceRepository.findById(currentUserSpaceId).orElseThrow()
        )

        return accountRepository.save(account).mapToResponse();
    }

    @Transactional
    fun updateAccount(id: UUID, accountRequest: AccountRequest) {
        val account = getAccountById(id)
        val oldBalance = account.balance;

        account.name = accountRequest.name
        account.type = AccountType.fromId(accountRequest.typeId)
        account.currency = Currency.fromCode(accountRequest.currencyCode)
        account.balance = accountRequest.balance ?: BigDecimal.ZERO

        accountRepository.save(account)

        if (oldBalance != accountRequest.balance) {
            createBalanceCorrectionTransaction(account, oldBalance)
        }
    }

    private fun createBalanceCorrectionTransaction(account: Account, oldBalance: BigDecimal) {
        val newAmount = account.balance.subtract(oldBalance)
        val transaction = Transaction(
            id = null,
            name = "Balance correction",
            notes = "Happened due to account update",
            amount = newAmount,
            currency = account.currency,
            fromAccount = account,
            toAccount = null,
            date = LocalDateTime.now(),
            category = null,
            type = if (newAmount > BigDecimal.ZERO) TransactionType.INCOME else TransactionType.EXPENSE,
            createdDate = LocalDateTime.now()
        )
        transactionRepository.save(transaction)
    }
}
