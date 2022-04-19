package no.nav.syfo.norg2.client

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.ereg.COUNT_CALL_EREG_ORGANISASJON_FAIL
import no.nav.syfo.ereg.COUNT_CALL_EREG_ORGANISASJON_SUCCESS
import no.nav.syfo.norg2.domain.ArbeidsfordelingCriteria
import no.nav.syfo.norg2.domain.NorgEnhet

class Norg2Client(
    val baseUrl: String,
) {
    private val httpClient = httpClientDefault()

    private val norg2ArbeidsfordelingBestmatchUrl: String = "$baseUrl$NORG2_ARBEIDSFORDELING_BESTMATCH_PATH"

    suspend fun arbeidsfordeling(
        arbeidsfordelingCriteria: ArbeidsfordelingCriteria,
    ): List<NorgEnhet> {
        try {
            val response = httpClient.post(norg2ArbeidsfordelingBestmatchUrl) {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                setBody(arbeidsfordelingCriteria)
            }
            COUNT_CALL_EREG_ORGANISASJON_SUCCESS.increment()
            return response.body()
        } catch (responseException: ResponseException) {
            COUNT_CALL_EREG_ORGANISASJON_FAIL.increment()
            throw responseException
        }
    }

    companion object {
        private const val NORG2_BASE_PATH = "/norg2/api/v1"
        const val NORG2_ARBEIDSFORDELING_BESTMATCH_PATH = "$NORG2_BASE_PATH/arbeidsfordeling/enheter/bestmatch"
    }
}
