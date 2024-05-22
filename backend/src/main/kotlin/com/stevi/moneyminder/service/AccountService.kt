package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.repository.AccountRepository
import com.stevi.moneyminder.repository.projection.AccountMonoBankTokenProjection
import java.math.BigDecimal
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(val accountRepository: AccountRepository) {

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
}
