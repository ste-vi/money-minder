package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.MonoBankInfo
import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.entity.Transaction
import com.stevi.moneyminder.entity.TransactionType
import com.stevi.moneyminder.entity.applyRule
import com.stevi.moneyminder.entity.mapToResponse
import com.stevi.moneyminder.exceptions.ResourceNotFoundException
import com.stevi.moneyminder.model.request.LinkMonoBankAccountRequest
import com.stevi.moneyminder.model.response.MonoBankAccountResponse
import com.stevi.moneyminder.model.response.MonoBankTransactionResponse
import com.stevi.moneyminder.repository.MonoBankInfoRepository
import com.stevi.moneyminder.repository.RuleRepository
import com.stevi.moneyminder.repository.SpaceRepository
import com.stevi.moneyminder.repository.TransactionRepository
import com.stevi.moneyminder.util.SecurityUtil
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class MonoBankService(
    private val monoBankInfoRepository: MonoBankInfoRepository,
    private val spaceRepository: SpaceRepository,
    private val accountService: AccountService,
    private val restTemplate: RestTemplate,
    private val transactionRepository: TransactionRepository,
    private val ruleRepository: RuleRepository,
    @Value("\${monobank.api.url}") private val monoBankUrl: String
) {

    @Transactional
    fun linkClient(spaceId: UUID, clientToken: String) {
        val clientId = fetchClientInfo(clientToken)
        if (clientId == null) {
            throw RuntimeException("Error fetching client info");
        } else {
            saveMonoBankInfo(spaceId, clientId, clientToken)
        }
    }

    private fun fetchClientInfo(clientToken: String): Any? {
        val uri = "$monoBankUrl/personal/client-info";

        val headers = HttpHeaders();
        headers.set("X-Token", "$clientToken")

        val requestEntity = HttpEntity<Any>(headers)

        val response = restTemplate.exchange<Map<String, Any>>(
            uri,
            HttpMethod.GET,
            requestEntity
        )

        if (response.statusCode != HttpStatusCode.valueOf(200)) {
            throw RuntimeException("Error fetching client info");
        }

        return response.body?.let { body -> body["clientId"] }
    }

    private fun saveMonoBankInfo(spaceId: UUID, clientId: Any, clientToken: String) {
        val space = spaceRepository.findById(spaceId).orElseThrow { ResourceNotFoundException("Entity not found") }
        val monoBankInfo = MonoBankInfo(null, clientId.toString(), clientToken, space)
        monoBankInfoRepository.save(monoBankInfo);
    }

    fun fetchAccounts(spaceId: UUID): List<MonoBankAccountResponse> {
        val monoBankInfo =
            monoBankInfoRepository.findBySpaceId(spaceId).orElseThrow { ResourceNotFoundException("Entity not found") }

        val uri = "$monoBankUrl/personal/client-info";

        val headers = HttpHeaders();
        headers.set("X-Token", "${monoBankInfo.token}")

        val requestEntity = HttpEntity<Any>(headers)

        val response = restTemplate.exchange<Map<String, Any>>(
            uri,
            HttpMethod.GET,
            requestEntity
        )

        if (response.statusCode != HttpStatusCode.valueOf(200)) {
            throw RuntimeException("Error fetching client info");
        }

        val accountsMap = response.body?.get("accounts") as? List<Map<String, Any>> ?: emptyList()

        val linkedMonoBankIds = accountService.getMonobankAccountIds(spaceId)

        return accountsMap.map { account ->
            MonoBankAccountResponse(
                id = account["id"] as String,
                type = account["type"] as String,
                balance = (account["balance"].toString()).toBigDecimal().divide(BigDecimal(100)),
                currency = Currency.fromCode(account["currencyCode"] as Int).mapToResponse(),
                maskedPan = (account["maskedPan"] as List<String>).firstOrNull(),
                iban = account["iban"] as String,
                isLinked = linkedMonoBankIds.contains(account["id"] as String)
            )
        }
    }

    @Transactional
    fun linkAccount(spaceId: UUID, request: LinkMonoBankAccountRequest) {
        val space = spaceRepository.findById(spaceId).orElseThrow { ResourceNotFoundException("Entity not found") }

        if (accountService.existsBySpaceIdAndMonoBankId(spaceId, request.id)) {
            throw RuntimeException("Account already linked");
        }

        val currency = Currency.fromCode(request.currencyCode)
        val accountName = "Monobank ${request.type} (${currency.name})" // todo: check it if for all cards is required?

        val savedAccount = accountService.saveAccount(
            Account(
                id = null,
                name = accountName,
                description = "Monobank | " + (request.maskedPan ?: request.iban),
                balance = request.balance,
                currency = currency,
                type = AccountType.BANK_ACCOUNTS,
                monoBankId = request.id,
                space = space,
                createdDate = LocalDateTime.now()
            )
        )

        val monoBankInfo =
            monoBankInfoRepository.findBySpaceId(spaceId).orElseThrow { ResourceNotFoundException("Entity not found") }

        updateRecentTransactionsFromMono(account = savedAccount, monoBankInfo.token)
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

    @Transactional
    fun updateRecentTransactionsFromMono(account: Account, monoBankToken: String) {
        val monoBankAccountId = account.monoBankId ?: throw RuntimeException();

        val monoTransactions =
            fetchResentTransactions(monoBankAccountId, monoBankToken)

        val recentTransactionsMonoIds =
            transactionRepository.findMonoBankIdsByDateGreaterThan(
                account.space.id!!,
                LocalDateTime.now().minusMonths(1)
            )

        val rules = ruleRepository.findAllBySpaceIdOrderByConditionTextToApplyAsc(account.space.id!!)

        val newTransactions = monoTransactions.stream()
            .filter { monoTransaction -> !recentTransactionsMonoIds.contains(monoTransaction.id) }
            .map { monoTransaction ->
                val transactionType =
                    if (monoTransaction.operationAmount.toBigDecimal() > BigDecimal.ZERO) TransactionType.INCOME else TransactionType.EXPENSE

                val date = LocalDateTime.ofEpochSecond(monoTransaction.time, 0, ZoneOffset.UTC)

                val amount = monoTransaction.operationAmount.toBigDecimal()
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

                rules.stream().forEach { rule -> transaction.applyRule(rule) }

                return@map transaction
            }.toList()

        if (newTransactions.isNotEmpty()) {
            accountService.updateAccountBalanceFromMonoBank(account, monoTransactions.first().balance)
        }

        transactionRepository.saveAll(newTransactions)
    }

    @Transactional
    fun refreshRecentTransactionsFromMonoForSpace(spaceId: UUID) {
        accountService.getAllMonobankAccountsForSpace(spaceId).stream().forEach { projection ->
            updateRecentTransactionsFromMono(projection.getAccount(), projection.getMonoBankToken())
        }
    }
}
