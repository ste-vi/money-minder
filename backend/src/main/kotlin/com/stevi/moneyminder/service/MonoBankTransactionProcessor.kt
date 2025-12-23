package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.Transaction
import com.stevi.moneyminder.entity.TransactionType
import com.stevi.moneyminder.entity.applyRule
import com.stevi.moneyminder.model.response.MonoBankTransactionResponse
import com.stevi.moneyminder.repository.RuleRepository
import com.stevi.moneyminder.repository.TransactionRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Component
class MonoBankTransactionProcessor(
    private val accountService: AccountService,
    private val restTemplate: RestTemplate,
    private val transactionRepository: TransactionRepository,
    private val ruleRepository: RuleRepository,
    private val exchangeService: ExchangeService,
    @Value("\${monobank.api.url}") private val monoBankUrl: String
) {

    private val logger = LoggerFactory.getLogger(MonoBankTransactionProcessor::class.java)

    @Transactional
    fun updateRecentTransactionsFromMono(account: Account, monoBankToken: String) {
        logger.info("Fetching mono transactions for account: " + account.name)
        val monoBankAccountId = account.monoBankId ?: throw RuntimeException();

        try {
            val monoTransactions =
                fetchResentTransactions(monoBankAccountId, monoBankToken)

            val recentTransactionsMonoIds =
                transactionRepository.findMonoBankIdsByDateGreaterThan(
                    account.space.id!!,
                    LocalDateTime.now().minusDays(33)
                )

            val rules = ruleRepository.findAllBySpaceIdOrderByConditionTextToApplyAsc(account.space.id!!)

            val newTransactions = monoTransactions.stream()
                .filter { monoTransaction -> !recentTransactionsMonoIds.contains(monoTransaction.id) }
                .map { monoTransaction ->
                    val transactionType =
                        if (monoTransaction.operationAmount.toBigDecimal() > BigDecimal.ZERO) TransactionType.INCOME else TransactionType.EXPENSE

                    val now = LocalDateTime.now()
                    val zone = ZoneId.of("Europe/Kyiv")
                    val zoneOffSet = zone.rules.getOffset(now)
                    val date = LocalDateTime.ofEpochSecond(monoTransaction.time, 0, zoneOffSet)

                    val amount = monoTransaction.amount.toBigDecimal()
                        .divide(BigDecimal.valueOf(100))
                        .abs()

                    val transaction = Transaction(
                        id = null,
                        name = monoTransaction.description,
                        notes = monoTransaction.comment,
                        amount = amount,
                        currency = account.currency,
                        date = date,
                        monoBankId = monoTransaction.id,
                        account = account,
                        fromAccount = null,
                        toAccount = null,
                        category = null,
                        type = transactionType,
                        createdDate = date,
                        currencyRate = null
                    )

                    rules.stream().forEach { rule -> transaction.applyRule(rule) }

                    if (account.space.primaryCurrency.code != account.currency.code) {
                        val exchangeRate = exchangeService.fetchExchangeRates().find { rate ->
                            (rate.currencyCodeA == account.currency.code && rate.currencyCodeB == account.space.primaryCurrency.code)
                                    || (rate.currencyCodeA == account.space.primaryCurrency.code && rate.currencyCodeB == account.currency.code)
                        } ?: throw RuntimeException("Exchange rate not found")
                        transaction.currencyRate = BigDecimal.valueOf(exchangeRate.rateBuy)
                    }

                    return@map transaction
                }.toList()

            if (newTransactions.isNotEmpty()) {
                logger.info("Inserting {} new transactions into {} account", newTransactions.size, account.name)
                accountService.updateAccountBalanceFromMonoBank(account, monoTransactions.first().balance)
                transactionRepository.saveAll(newTransactions)
            }
            accountService.updateAccountTransactionSyncDate(account)
        } catch (httpEx: HttpClientErrorException) {
            if (httpEx.statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                logger.warn("Rate limit hit for account ${account.id}. Spring Retry will handle the backoff.")
                throw httpEx
            }
            throw httpEx
        } catch (ex: Exception) {
            throw RuntimeException("Error fetching recent transactions from Mono: ${ex.message}")
        }
    }

    fun fetchResentTransactions(accountId: String, token: String): List<MonoBankTransactionResponse> {
        val fromEpochTime = LocalDateTime.now().minusDays(31).plusHours(1).toEpochSecond(ZoneOffset.UTC)
        val uri = "$monoBankUrl/personal/statement/$accountId/$fromEpochTime";

        val headers = HttpHeaders();
        headers.set("X-Token", "${token}")

        val requestEntity = HttpEntity<Any>(headers)

        val response = restTemplate.exchange<List<MonoBankTransactionResponse>>(
            uri,
            HttpMethod.GET,
            requestEntity
        )

        if (response.statusCode != HttpStatusCode.valueOf(200)) {
            throw RuntimeException("Error fetching client info");
        }

        return response.body ?: emptyList()
    }

}
