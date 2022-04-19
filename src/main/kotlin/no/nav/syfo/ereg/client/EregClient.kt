package no.nav.syfo.ereg.client

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.ereg.COUNT_CALL_EREG_ORGANISASJON_FAIL
import no.nav.syfo.ereg.COUNT_CALL_EREG_ORGANISASJON_SUCCESS
import no.nav.syfo.ereg.domain.EregOrganisasjonResponse
import no.nav.syfo.util.bearerHeader

class EregClient(
    val baseUrl: String,
    val stsClient: StsClient,
) {
    private val httpClient = httpClientDefault()

    private val eregOrganisasjonUrl: String = "$baseUrl$EREG_PATH"

    suspend fun organisasjon(
        orgNr: String,
    ): EregOrganisasjonResponse {
        val stsToken = stsClient.token()
        try {
            val url = "$eregOrganisasjonUrl/$orgNr"
            val response = httpClient.get(url) {
                header(HttpHeaders.Authorization, bearerHeader(stsToken))
                accept(ContentType.Application.Json)
            }
            COUNT_CALL_EREG_ORGANISASJON_SUCCESS.increment()
            return response.body()
        } catch (responseException: ResponseException) {
            COUNT_CALL_EREG_ORGANISASJON_FAIL.increment()
            throw responseException
        }
    }

    companion object {
        const val EREG_PATH = "/ereg/api/v1/organisasjon"
    }
}
