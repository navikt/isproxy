package no.nav.syfo.client.dokdist

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.metric.COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL
import no.nav.syfo.metric.COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS
import no.nav.syfo.util.bearerHeader
import org.slf4j.LoggerFactory

class DokdistClient(
    dokdistBaseUrl: String,
    val stsClient: StsClient,
) {

    private val distribuerJournalpostUrl: String = "$dokdistBaseUrl$DISTRIBUER_JOURNALPOST_PATH"
    private val httpClient = httpClientDefault()

    suspend fun distribuerJournalpost(
        dokdistRequest: DokdistRequest,
        callId: String
    ): DokdistResponse? {
        try {
            val stsToken = stsClient.token()
            val response: DokdistResponse = httpClient.post(distribuerJournalpostUrl) {
                header(HttpHeaders.Authorization, bearerHeader(stsToken))
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                body = dokdistRequest
            }
            COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS.increment()
            return response
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e.response, e.message, callId)
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e.response, e.message, callId)
        }
        return null
    }

    private fun handleUnexpectedResponseException(
        response: HttpResponse,
        message: String?,
        callId: String,
    ) {
        log.error(
            "Error while requesting dokdist to distribuere journalpost with {}, {}, {}",
            StructuredArguments.keyValue("statusCode", response.status.value.toString()),
            StructuredArguments.keyValue("message", message),
            StructuredArguments.keyValue("callId", callId),
        )
        COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL.increment()
    }

    companion object {
        const val DISTRIBUER_JOURNALPOST_PATH = "/rest/v1/distribuerJournalpost"
        private val log = LoggerFactory.getLogger(DokdistClient::class.java)
    }
}
