package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Account
import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.MonoBankInfo
import com.stevi.moneyminder.entity.AccountType
import com.stevi.moneyminder.model.request.LinkMonoBankAccountRequest
import com.stevi.moneyminder.model.response.MonoBankAccountResponse
import com.stevi.moneyminder.model.response.MonoBankTransactionResponse
import com.stevi.moneyminder.repository.MonoBankInfoRepository
import com.stevi.moneyminder.repository.SpaceRepository
import java.math.BigDecimal
import java.util.*
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
        val space = spaceRepository.findById(spaceId).orElseThrow()
        val monoBankInfo = MonoBankInfo(null, clientId.toString(), clientToken, space)
        monoBankInfoRepository.save(monoBankInfo);
    }

    fun fetchAccounts(spaceId: UUID): List<MonoBankAccountResponse> {
        val monoBankInfo = monoBankInfoRepository.findBySpaceId(spaceId).orElseThrow()

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

        return accountsMap.filter { account -> !linkedMonoBankIds.contains(account["id"] as String) }.map { account ->
            MonoBankAccountResponse(
                id = account["id"] as String,
                type = account["type"] as String,
                balance = (account["balance"].toString()).toBigDecimal().divide(BigDecimal(100)),
                currencyCode = account["currencyCode"] as Int,
                maskedPan = (account["maskedPan"] as List<String>).firstOrNull(),
                iban = account["iban"] as String,
            )
        }
    }

    fun fetchResentTransactions(accountId: String, token: String): List<MonoBankTransactionResponse> {
        val uri = "$monoBankUrl/personal/statement/$accountId/1715320009";

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
    fun linkAccount(spaceId: UUID, request: LinkMonoBankAccountRequest) {
        val space = spaceRepository.findById(spaceId).orElseThrow()

        if (accountService.existsBySpaceIdAndMonoBankId(spaceId, request.id)) {
            throw RuntimeException("Account already linked");
        }

        val accountName = "Mono " + request.type
        accountService.saveAccount(
            Account(
                null,
                accountName,
                request.balance,
                Currency.fromCode(request.currencyCode),
                AccountType.BANK_ACCOUNTS,
                request.id,
                space
            )
        )
    }
}
