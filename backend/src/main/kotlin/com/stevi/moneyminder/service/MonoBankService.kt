package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.MonoBankInfo
import com.stevi.moneyminder.model.response.MonoBankAccountResponse
import com.stevi.moneyminder.repository.MonoBankInfoRepository
import com.stevi.moneyminder.repository.UserRepository
import java.util.UUID
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
    val monoBankInfoRepository: MonoBankInfoRepository,
    val userRepository: UserRepository,
    val restTemplate: RestTemplate,
    @Value("\${monobank.api.url}") val monoBankUrl: String
) {

    @Transactional
    fun linkClient(userId: UUID, clientToken: String) {
        val clientId = fetchClientInfo(clientToken)
        if (clientId == null) {
            throw RuntimeException("Error fetching client info");
        } else {
            saveMonoBankInfo(userId, clientId, clientToken)
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

    private fun saveMonoBankInfo(userId: UUID, clientId: Any, clientToken: String) {
        val user = userRepository.findById(userId).orElseThrow()
        val monoBankInfo = MonoBankInfo(null, clientId.toString(), clientToken, user)
        monoBankInfoRepository.save(monoBankInfo);
    }

    fun fetchAccounts(userId: UUID): List<MonoBankAccountResponse> {
        val monoBankInfo = monoBankInfoRepository.findByUserId(userId).orElseThrow()

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

        return accountsMap.map { account ->
            MonoBankAccountResponse(
                id = account["id"] as String,
                type = account["type"] as String,
                balance = (account["balance"].toString()).toBigDecimal(),
                currencyCode = account["currencyCode"] as Int,
                maskedPan = (account["maskedPan"] as List<String>).firstOrNull() ?: null,
                iban = account["iban"] as String,
            )
        }
    }

}
