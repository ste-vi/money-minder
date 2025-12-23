package com.stevi.moneyminder.scheduler

import com.stevi.moneyminder.service.AccountService
import com.stevi.moneyminder.service.MonoBankService
import java.util.concurrent.TimeUnit
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class MonoBankTransactionScheduler(
    private val monoBankService: MonoBankService,
    private val accountService: AccountService,
) {

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    fun run() {
        accountService.getAllMonobankAccountsAvailableForTransactionSync().forEach { projection ->
            monoBankService.syncAccountTransactionUpdateAsync(projection.getAccount(), projection.getMonoBankToken())
        }
    }
}