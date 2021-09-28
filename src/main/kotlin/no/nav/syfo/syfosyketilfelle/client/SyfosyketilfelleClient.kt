package no.nav.syfo.syfosyketilfelle.client

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.domain.AktorId
import no.nav.syfo.syfosyketilfelle.COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_FAIL
import no.nav.syfo.syfosyketilfelle.COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_SUCCESS
import no.nav.syfo.syfosyketilfelle.domain.KOppfolgingstilfellePerson
import no.nav.syfo.util.bearerHeader

class SyfosyketilfelleClient(
    baseUrl: String,
    private val stsClient: StsClient,
) {
    private val httpClient = httpClientDefault()

    private val syfosyketilfelleOppfolgingstilfellePersonUrl: String =
        "$baseUrl$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH"

    suspend fun oppfolgingstilfellePerson(
        aktorId: AktorId,
    ): KOppfolgingstilfellePerson? {
        val stsToken = stsClient.token()
        try {
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
        } catch (responseException: ResponseException) {
            COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_FAIL.increment()
            throw responseException
        }
    }

    companion object {
        const val SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH = "/kafka/oppfolgingstilfelle/beregn"
    }
}
