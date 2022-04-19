package no.nav.syfo.kuhrsar.client

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.azuread.AzureAdClient
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.domain.PersonIdent
import no.nav.syfo.kuhrsar.model.Samhandler
import no.nav.syfo.util.*

class KuhrSarClient(
    private val azureAdClient: AzureAdClient,
    private val kuhrsarClientId: String,
    private val kuhrsarUrl: String,
) {

    private val httpClient = httpClientDefault()

    suspend fun getSamhandler(behandlerIdent: PersonIdent): List<Samhandler> {
        val token = azureAdClient.getSystemToken(kuhrsarClientId)
            ?: throw RuntimeException("Failed to send request to KuhrSar: No token was found")
        val response: HttpResponse = httpClient.get("$kuhrsarUrl$KUHRSAR_PATH") {
            accept(ContentType.Application.Json)
            header(HttpHeaders.Authorization, bearerHeader(token.accessToken))
            parameter("ident", behandlerIdent.value)
        }
        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw RuntimeException("Call to KuhrSar failed with http status: ${response.status}")
        }
    }

    companion object {
        const val KUHRSAR_PATH = "/sar/rest/v2/samh"
    }
}
