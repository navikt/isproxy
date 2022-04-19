package no.nav.syfo.client.sts

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.metric.COUNT_CALL_STS_FAIL
import no.nav.syfo.metric.COUNT_CALL_STS_SUCCESS
import no.nav.syfo.util.basicHeader
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class StsClient(
    val properties: StsClientProperties,
) {
    private var cachedOidcToken: Token? = null

    private val httpClient = httpClientDefault()
    private val url = "${properties.baseUrl}/rest/v1/sts/token?grant_type=client_credentials&scope=openid"

    suspend fun token(): String {
        if (Token.shouldRenew(cachedOidcToken)) {
            try {
                val authHeader = basicHeader(
                    credentialUsername = properties.serviceuserUsername,
                    credentialPassword = properties.serviceuserPassword,
                )
                val response: HttpResponse = httpClient.get(url) {
                    header(HttpHeaders.Authorization, authHeader)
                    accept(ContentType.Application.Json)
                }
                cachedOidcToken = response.body<Token>()
                COUNT_CALL_STS_SUCCESS.increment()
            } catch (e: ResponseException) {
                log.error(
                    "Error while requesting sts token with status: ${e.response.status.value}",
                )
                COUNT_CALL_STS_FAIL.increment()
                throw e
            }
        }

        return cachedOidcToken!!.access_token
    }

    data class Token(
        val access_token: String,
        val token_type: String,
        val expires_in: Int
    ) {
        val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expires_in - 10L)

        companion object {
            fun shouldRenew(token: Token?): Boolean {
                return token == null || isExpired(token)
            }

            private fun isExpired(token: Token): Boolean {
                return token.expirationTime.isBefore(LocalDateTime.now())
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StsClient::class.java)
    }
}
