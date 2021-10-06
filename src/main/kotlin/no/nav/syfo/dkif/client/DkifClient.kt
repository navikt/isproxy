package no.nav.syfo.dkif.client

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.dkif.COUNT_CALL_DKIF_KONTAKTINFORMASJON_FAIL
import no.nav.syfo.dkif.COUNT_CALL_DKIF_KONTAKTINFORMASJON_SUCCESS
import no.nav.syfo.dkif.domain.DigitalKontaktinfoBolk
import no.nav.syfo.util.*

class DkifClient(
    baseUrl: String,
    private val stsClient: StsClient,
) {
    private val httpClient = httpClientDefault()

    private val dkifKontaktinformasjonUrl: String = "$baseUrl$DKIF_KONTAKTINFO_PATH"

    suspend fun digitalKontaktinfoBolk(
        callId: String,
        personIdentNumberList: String,
    ): DigitalKontaktinfoBolk {
        val stsToken = stsClient.token()
        try {
            val response: DigitalKontaktinfoBolk = httpClient.get(dkifKontaktinformasjonUrl) {
                header(HttpHeaders.Authorization, bearerHeader(stsToken))
                header(NAV_CONSUMER_ID_HEADER, NAV_CONSUMER_APP_ID)
                header(NAV_CALL_ID_HEADER, callId)
                header(NAV_PERSONIDENTER_HEADER, personIdentNumberList)
                accept(ContentType.Application.Json)
            }
            COUNT_CALL_DKIF_KONTAKTINFORMASJON_SUCCESS.increment()
            return response
        } catch (responseException: ResponseException) {
            COUNT_CALL_DKIF_KONTAKTINFORMASJON_FAIL.increment()
            throw responseException
        }
    }

    companion object {
        const val DKIF_KONTAKTINFO_PATH = "/api/v1/personer/kontaktinformasjon"

        const val NAV_PERSONIDENTER_HEADER = "Nav-Personidenter"
    }
}
