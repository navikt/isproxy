package no.nav.syfo.axsys.client

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.axsys.COUNT_CALL_AXSYS_ENHET_BRUKERE_FAIL
import no.nav.syfo.axsys.COUNT_CALL_AXSYS_ENHET_BRUKERE_SUCCESS
import no.nav.syfo.axsys.domain.AxsysVeileder
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.util.*

class AxsysClient(
    baseUrl: String,
) {
    private val httpClient = httpClientDefault()

    private val axsysEnhetUrl: String = "$baseUrl$AXSYS_ENHET_BASE_PATH"

    suspend fun veiledere(
        callId: String,
        enhetNr: String,
    ): List<AxsysVeileder> {
        try {
            val url = "$axsysEnhetUrl/$enhetNr$AXSYS_BRUKERE_PATH"
            val response: List<AxsysVeileder> = httpClient.get(url) {
                header(NAV_CALL_ID_HEADER, callId)
                header(NAV_CONSUMER_ID_HEADER, NAV_CONSUMER_APP_ID)
                accept(ContentType.Application.Json)
            }
            COUNT_CALL_AXSYS_ENHET_BRUKERE_SUCCESS.increment()
            return response
        } catch (responseException: ResponseException) {
            COUNT_CALL_AXSYS_ENHET_BRUKERE_FAIL.increment()
            throw responseException
        }
    }

    companion object {
        const val AXSYS_ENHET_BASE_PATH = "/api/v1/enhet"
        const val AXSYS_BRUKERE_PATH = "/brukere"
    }
}
