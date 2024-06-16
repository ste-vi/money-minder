package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.AccountBalanceHistory
import com.stevi.moneyminder.model.response.NetWorthHistory
import com.stevi.moneyminder.repository.AccountBalanceHistoryRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.UK)

@Service
class AccountBalanceHistoryService(
    private val repository: AccountBalanceHistoryRepository,
    private val exchangeService: ExchangeService
) {

    @Transactional(readOnly = true)
    fun getHistoryForLastYear(spaceId: UUID): List<NetWorthHistory> {
        val historyList = repository.findLastBalanceHistoryBySpaceId(spaceId)

        return historyList.groupBy { it.date }
            .map { (date, history) ->
                val totalBalance = history.sumOf { it.balance }
                val formattedDate = date.format(DATE_FORMATTER)
                NetWorthHistory(totalBalance, formattedDate)
            }
    }

    @Transactional
    fun saveHistory(account: Account) {
        val alreadyExistsForToday = repository.existsByAccountIdAndDate(account.id!!, LocalDate.now())
        if (alreadyExistsForToday) {
            return;
        }

        val balance = convertBalanceToSpaceCurrency(account)

        repository.save(
            AccountBalanceHistory(
                id = null,
                balance = balance,
                date = LocalDate.now(),
                account = account
            )
        )
    }

    private fun convertBalanceToSpaceCurrency(account: Account): BigDecimal {
        var balance = account.balance
        if (account.currency != account.space.primaryCurrency) {
            val exchangeRates = exchangeService.fetchExchangeRates()
            val exchangeRate = exchangeRates.find { rate ->
                rate.currencyCodeA == account.currency.code && rate.currencyCodeB == account.space.primaryCurrency.code
            } ?: throw RuntimeException("Exchange rate not found")

            balance = balance.multiply(BigDecimal.valueOf(exchangeRate.rateBuy))
        }
        return balance
    }
}
