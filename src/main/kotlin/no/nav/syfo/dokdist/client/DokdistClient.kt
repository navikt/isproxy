package no.nav.syfo.dokdist.client

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.dokdist.COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL
import no.nav.syfo.dokdist.COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS
import no.nav.syfo.dokdist.domain.DokdistRequest
import no.nav.syfo.dokdist.domain.DokdistResponse
import no.nav.syfo.util.NAV_CONSUMER_TOKEN_HEADER
import no.nav.syfo.util.bearerHeader

class DokdistClient(
    dokdistBaseUrl: String,
    val stsClient: StsClient,
) {

    private val distribuerJournalpostUrl: String = "$dokdistBaseUrl$DISTRIBUER_JOURNALPOST_PATH"
    private val httpClient = httpClientDefault()

    suspend fun distribuerJournalpost(
        dokdistRequest: DokdistRequest,
    ): DokdistResponse {
        val stsToken = stsClient.token()
        try {
            val response: DokdistResponse = httpClient.post(distribuerJournalpostUrl) {
                header(HttpHeaders.Authorization, bearerHeader(stsToken))
                header(NAV_CONSUMER_TOKEN_HEADER, bearerHeader(stsToken))
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                body = dokdistRequest
            }
            COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS.increment()
            return response
        } catch (responseException: ResponseException) {
            COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL.increment()
            throw responseException
        }
    }

    companion object {
        const val DISTRIBUER_JOURNALPOST_PATH = "/rest/v1/distribuerjournalpost"
    }
}
