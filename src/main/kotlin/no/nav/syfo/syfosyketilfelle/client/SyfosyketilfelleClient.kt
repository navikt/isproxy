package no.nav.syfo.syfosyketilfelle.client

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.domain.AktorId
import no.nav.syfo.syfosyketilfelle.COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_FAIL
import no.nav.syfo.syfosyketilfelle.COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_SUCCESS
import no.nav.syfo.syfosyketilfelle.domain.KOppfolgingstilfellePerson
import no.nav.syfo.util.bearerHeader
import org.slf4j.LoggerFactory

class SyfosyketilfelleClient(
    baseUrl: String,
    private val stsClient: StsClient,
) {
    private val httpClient = httpClientDefault()

    private val syfosyketilfelleOppfolgingstilfellePersonUrl: String =
        "$baseUrl$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH"

    suspend fun oppfolgingstilfellePerson(
        callId: String,
        aktorId: AktorId,
    ): KOppfolgingstilfellePerson? {
        try {
            val stsToken = stsClient.token()
            val url = "$syfosyketilfelleOppfolgingstilfellePersonUrl/${aktorId.value}"
            val response: HttpResponse = httpClient.get(url) {
                header(HttpHeaders.Authorization, bearerHeader(stsToken))
                accept(ContentType.Application.Json)
            }
            if (response.status == HttpStatusCode.NoContent) {
                return null
            }
            val responseBody = response.receive<KOppfolgingstilfellePerson>()
            COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_SUCCESS.increment()
            return responseBody
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
            "Error while requesting Response from Syfosyketilfelle {}, {}, {}",
            StructuredArguments.keyValue("statusCode", response.status.value.toString()),
            StructuredArguments.keyValue("message", message),
            StructuredArguments.keyValue("callId", callId),
        )
        COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_FAIL.increment()
    }

    companion object {
        const val SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH = "/kafka/oppfolgingstilfelle/beregn"
        private val log = LoggerFactory.getLogger(SyfosyketilfelleClient::class.java)
    }
}
