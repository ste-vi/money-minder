package com.stevi.moneyminder.scheduler

import com.stevi.moneyminder.service.AccountService
import com.stevi.moneyminder.service.MonoBankService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class MonoBankTransactionScheduler(
    private val monoBankService: MonoBankService,
    private val accountService: AccountService,
) {

    @OptIn(DelicateCoroutinesApi::class)
    @Scheduled(fixedRate = 1000 * 60 * 10)
    fun run() {
        accountService.getAllMonobankAccounts().stream().forEach { projection ->
            GlobalScope.async {
                monoBankService.fetchRecentTransactionsFromMono(projection.getAccount(), projection.getMonoBankToken())
            }
        }
    }
}