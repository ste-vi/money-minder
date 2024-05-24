package com.stevi.moneyminder.scheduler

import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.Transaction
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
    private val transactionRepository: TransactionRepository
) {

    @OptIn(DelicateCoroutinesApi::class)
    @Scheduled(fixedRate = 1000 * 60 * 3)
    fun run() {
        accountService.getAllMonobankAccounts().stream().forEach { projection ->
            GlobalScope.async {
                fetchRecentTransactionsFromMono(projection)
            }
        }
    }

    private fun fetchRecentTransactionsFromMono(projection: AccountMonoBankTokenProjection) {
        val monoBankAccountId = projection.getAccount().monoBankId ?: throw RuntimeException();

        val monoTransactions =
            monoBankService.fetchResentTransactions(monoBankAccountId, projection.getMonoBankToken())

        val recentTransactionsMonoIds =
            transactionRepository.findMonoBankIdsByDateGreaterThan(LocalDateTime.now().minusMonths(1));

        val newTransactions = monoTransactions.stream()
            .filter { monoTransaction -> !recentTransactionsMonoIds.contains(monoTransaction.id) }
            .map { monoTransaction ->
                Transaction(
                    id = null,
                    name = monoTransaction.description,
                    notes = monoTransaction.comment,
                    amount = monoTransaction.amount.toBigDecimal().divide(BigDecimal.valueOf(100)),
                    currency = Currency.fromCode(monoTransaction.currencyCode),
                    date = LocalDateTime.ofEpochSecond(monoTransaction.time, 0, ZoneOffset.UTC),
                    monoBankId = monoTransaction.id,
                    fromAccount = projection.getAccount(),
                    toAccount = null,
                    category = null
                )
            }.toList()

        if (monoTransactions.isNotEmpty()) {
            accountService.updateAccountBalance(projection.getAccount(), monoTransactions.first().balance)
        }

        transactionRepository.saveAll(newTransactions)
    }
}