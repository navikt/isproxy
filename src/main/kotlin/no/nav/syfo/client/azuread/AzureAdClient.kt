package no.nav.syfo.client.azuread

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientProxy
import org.slf4j.LoggerFactory

class AzureAdClient(
    private val aadAppClient: String,
    private val aadAppSecret: String,
    private val aadTokenEndpoint: String,
) {
    private val httpClient = httpClientProxy()

    suspend fun getSystemToken(scopeClientId: String): AzureAdToken? {
        return getAccessToken(
            Parameters.build {
                append("client_id", aadAppClient)
                append("client_secret", aadAppSecret)
                append("grant_type", "client_credentials")
                append("scope", "api://$scopeClientId/.default")
            }
        )?.toAzureAdToken()
    }

    private suspend fun getAccessToken(formParameters: Parameters): AzureAdTokenResponse? {
        return try {
            val response: HttpResponse = httpClient.post(aadTokenEndpoint) {
                accept(ContentType.Application.Json)
                body = FormDataContent(formParameters)
            }
            response.receive<AzureAdTokenResponse>()
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e)
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e)
        }
    }

    private fun handleUnexpectedResponseException(
        responseException: ResponseException
    ): AzureAdTokenResponse? {
        log.error(
            "Error while requesting AzureAdAccessToken with statusCode=${responseException.response.status.value}",
            responseException
        )
        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(AzureAdClient::class.java)
    }
}
