package no.nav.syfo.client.dokdist

import io.ktor.client.features.*
import io.ktor.client.statement.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.syfo.metric.COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL
import no.nav.syfo.metric.COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS
import org.slf4j.LoggerFactory
import java.util.*

class DokdistClient(dokdistBaseUrl: String) {

    private val distribuerJournalpostUrl: String = "$dokdistBaseUrl$DISTRIBUER_JOURNALPOST_PATH"

    suspend fun distribuerJournalpost(
        dokdistRequest: DokdistRequest,
        callId: String
    ): DokdistResponse? {
        return try {
            // TODO: Call dokdist api with sts-token ?
            COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS.increment()
            DokdistResponse(
                bestillingsId = UUID.randomUUID().toString()
            )
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e.response, e.message)
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e.response, e.message)
        }
    }

    private fun handleUnexpectedResponseException(
        response: HttpResponse,
        message: String?,
    ): DokdistResponse? {
        log.error(
            "Error while requesting dokdist to distribuere journalpost with {}, {}",
            StructuredArguments.keyValue("statusCode", response.status.value.toString()),
            StructuredArguments.keyValue("message", message),
        )
        COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL.increment()
        return null
    }

    companion object {
        const val DISTRIBUER_JOURNALPOST_PATH = "/rest/v1/distribuerJournalpost"
        private val log = LoggerFactory.getLogger(DokdistClient::class.java)
    }
}
