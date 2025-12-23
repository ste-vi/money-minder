package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.MonoBankInfo
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
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class MonoBankService(
    private val monoBankInfoRepository: MonoBankInfoRepository,
    private val spaceRepository: SpaceRepository,
    private val accountService: AccountService,
    private val restTemplate: RestTemplate,
    private val monoBankTransactionProcessor: MonoBankTransactionProcessor,
    @Value("\${monobank.api.url}") private val monoBankUrl: String
) {

    private val logger = LoggerFactory.getLogger(MonoBankService::class.java)

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
        val accountName = "Monobank ${request.type} (${currency.name})"

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
                createdDate = LocalDateTime.now(),
                transactionsSyncDate = null
            )
        )

        val monoBankInfo =
            monoBankInfoRepository.findBySpaceId(spaceId).orElseThrow { ResourceNotFoundException("Entity not found") }

        monoBankTransactionProcessor.updateRecentTransactionsFromMono(account = savedAccount, monoBankInfo.token)
    }

    @Async
    @Retryable(
        retryFor = [HttpClientErrorException.TooManyRequests::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 121000)
    )
    fun syncAccountTransactionUpdateAsync(account: Account, token: String) {
        monoBankTransactionProcessor.updateRecentTransactionsFromMono(account, token)
    }

    @Recover
    fun recoverMonoUpdate(ex: HttpClientErrorException, account: Account, token: String) {
        logger.error("Failed to update transactions after 3 attempts for account ${account.name}: ${ex.message}")
    }

    @Transactional
    fun refreshRecentTransactionsFromMonoForSpace(spaceId: UUID) {
        accountService.getAllMonobankAccountsForSpace(spaceId).stream().forEach { projection ->
            monoBankTransactionProcessor.updateRecentTransactionsFromMono(projection.getAccount(), projection.getMonoBankToken())
        }
    }
}
