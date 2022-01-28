package no.nav.syfo.btsys.client

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.btsys.COUNT_CALL_BTSYS_SUSPENSJON_FAIL
import no.nav.syfo.btsys.COUNT_CALL_BTSYS_SUSPENSJON_SUCCESS
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.util.*

class BtsysClient(
    val baseUrl: String,
    val stsClient: StsClient,
    val serviceUserName: String,
) {
    private val httpClient = httpClientDefault()

    private val btsysUrl: String = "$baseUrl$BTSYS_PATH"

    suspend fun suspendert(
        personIdent: String,
        oppslagsdato: String,
        callId: String,
    ): Suspendert {
        val stsToken = stsClient.token()
        try {
            val response: Suspendert = httpClient.get(btsysUrl) {
                accept(ContentType.Application.Json)
                headers {
                    append(NAV_CALL_ID_HEADER, callId)
                    append(NAV_CONSUMER_ID_HEADER, serviceUserName)
                    append(NAV_PERSONIDENT_HEADER, personIdent)
                    append(HttpHeaders.Authorization, bearerHeader(stsToken))
                }
                parameter("oppslagsdato", oppslagsdato)
            }
            COUNT_CALL_BTSYS_SUSPENSJON_SUCCESS.increment()
            return response
        } catch (responseException: ResponseException) {
            COUNT_CALL_BTSYS_SUSPENSJON_FAIL.increment()
            throw responseException
        }
    }

    data class Suspendert(val suspendert: Boolean)

    companion object {
        const val BTSYS_PATH = "/api/v1/suspensjon/status"
    }
}
