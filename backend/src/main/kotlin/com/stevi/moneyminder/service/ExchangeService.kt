package com.stevi.moneyminder.service

import com.stevi.moneyminder.model.response.MonoBankExchangeRateResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class ExchangeService(
    private val restTemplate: RestTemplate,
    @Value("\${monobank.api.url}") private val monoBankUrl: String
) {

    @Cacheable("exchangeRates")
    fun fetchExchangeRates(): List<MonoBankExchangeRateResponse> {
        val uri = "$monoBankUrl/bank/currency";

        val response = restTemplate.exchange<List<MonoBankExchangeRateResponse>>(
            uri,
            HttpMethod.GET
        )

        return response.body ?: throw RuntimeException("Response body is null")
    }

    @Cacheable("exchangeRate")
    fun fetchExchangeRate(currencyCodeFrom: Int, currencyCodeTo: Int): Double {
        val uri = "$monoBankUrl/bank/currency";

        val response = restTemplate.exchange<List<MonoBankExchangeRateResponse>>(
            uri,
            HttpMethod.GET
        )

        val body = response.body ?: throw RuntimeException("Response body is null")

        return body.firstOrNull {
            it.currencyCodeA == currencyCodeFrom && it.currencyCodeB == currencyCodeTo
        }?.rateBuy ?: throw RuntimeException("Exchange rate not found")
    }
}
