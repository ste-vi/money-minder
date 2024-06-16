package com.stevi.moneyminder.scheduler

import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.Transaction
import com.stevi.moneyminder.entity.TransactionType
import com.stevi.moneyminder.entity.applyRule
import com.stevi.moneyminder.repository.RuleRepository
import com.stevi.moneyminder.repository.TransactionRepository
import com.stevi.moneyminder.repository.projection.AccountMonoBankTokenProjection
import com.stevi.moneyminder.service.AccountService
import com.stevi.moneyminder.service.MonoBankService
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class MonoBankTransactionScheduler(
    private val monoBankService: MonoBankService,
    private val accountService: AccountService,
    private val transactionRepository: TransactionRepository,
    private val ruleRepository: RuleRepository
) {

    @OptIn(DelicateCoroutinesApi::class)
    @Scheduled(fixedRate = 1000 * 60 * 10)
    fun run() {
        accountService.getAllMonobankAccounts().stream().forEach { projection ->
            GlobalScope.async {
                fetchRecentTransactionsFromMono(projection)
            }
        }
    }

    private fun fetchRecentTransactionsFromMono(projection: AccountMonoBankTokenProjection) {
        val account = projection.getAccount()
        val monoBankAccountId = account.monoBankId ?: throw RuntimeException();

        val monoTransactions =
            monoBankService.fetchResentTransactions(monoBankAccountId, projection.getMonoBankToken())

        val recentTransactionsMonoIds =
            transactionRepository.findMonoBankIdsByDateGreaterThan(LocalDateTime.now().minusMonths(1));

        val rules = ruleRepository.findAllBySpaceIdOrderByConditionTextToApplyAsc(account.space.id!!)

        val newTransactions = monoTransactions.stream()
            .filter { monoTransaction -> !recentTransactionsMonoIds.contains(monoTransaction.id) }
            .map { monoTransaction ->
                val transactionType =
                    if (monoTransaction.amount.toBigDecimal() > BigDecimal.ZERO) TransactionType.INCOME else TransactionType.EXPENSE

                val date = LocalDateTime.ofEpochSecond(monoTransaction.time, 0, ZoneOffset.UTC)

                val amount = monoTransaction.amount.toBigDecimal()
                    .divide(BigDecimal.valueOf(100))
                    .abs()

                val transaction = Transaction(
                    id = null,
                    name = monoTransaction.description,
                    notes = monoTransaction.comment,
                    amount = amount,
                    currency = Currency.fromCode(monoTransaction.currencyCode),
                    date = date,
                    monoBankId = monoTransaction.id,
                    fromAccount = account,
                    toAccount = null,
                    category = null,
                    type = transactionType,
                    createdDate = date
                )

                rules.stream().anyMatch { rule -> transaction.applyRule(rule) }

                return@map transaction
            }.toList()

        if (newTransactions.isNotEmpty()) {
            accountService.updateAccountBalance(account, monoTransactions.first().balance)
        }

        transactionRepository.saveAll(newTransactions)
    }
}