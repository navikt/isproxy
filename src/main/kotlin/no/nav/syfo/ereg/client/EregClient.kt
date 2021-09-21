package no.nav.syfo.ereg.client

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.ereg.COUNT_CALL_EREG_ORGANISASJON_FAIL
import no.nav.syfo.ereg.COUNT_CALL_EREG_ORGANISASJON_SUCCESS
import no.nav.syfo.ereg.domain.EregOrganisasjonResponse
import no.nav.syfo.util.bearerHeader
import org.slf4j.LoggerFactory

class EregClient(
    val baseUrl: String,
    val stsClient: StsClient,
) {
    private val httpClient = httpClientDefault()

    private val eregOrganisasjonUrl: String = "$baseUrl$EREG_PATH"

    suspend fun organisasjon(
        callId: String,
        orgNr: String,
    ): EregOrganisasjonResponse {
        try {
            val stsToken = stsClient.token()
            val url = "$eregOrganisasjonUrl/$orgNr"
            val response: EregOrganisasjonResponse = httpClient.get(url) {
                header(HttpHeaders.Authorization, bearerHeader(stsToken))
                accept(ContentType.Application.Json)
            }
            COUNT_CALL_EREG_ORGANISASJON_SUCCESS.increment()
            return response
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e.response, e.message, callId)
            throw e
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e.response, e.message, callId)
            throw e
        }
    }

    private fun handleUnexpectedResponseException(
        response: HttpResponse,
        message: String?,
        callId: String,
    ) {
        log.error(
            "Error while requesting Response from Ereg {}, {}, {}",
            StructuredArguments.keyValue("statusCode", response.status.value.toString()),
            StructuredArguments.keyValue("message", message),
            StructuredArguments.keyValue("callId", callId),
        )
        COUNT_CALL_EREG_ORGANISASJON_FAIL.increment()
    }

    companion object {
        const val EREG_PATH = "/ereg/api/v1/organisasjon"
        private val log = LoggerFactory.getLogger(EregClient::class.java)
    }
}
