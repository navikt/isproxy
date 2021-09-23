package no.nav.syfo.dokdist.api

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.dokdist.client.DokdistClient
import no.nav.syfo.dokdist.domain.DokdistRequest
import no.nav.syfo.util.getBearerHeader
import no.nav.syfo.util.getCallId
import no.nav.syfo.util.handleProxyError

const val dokDistBasePath = "/api/v1/dokdist"
const val distribuerJournalpostPath = "/distribuerjournalpost"

fun Route.registerDokdistApi(
    dokdistClient: DokdistClient,
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>
) {
    route(dokDistBasePath) {
        post(distribuerJournalpostPath) {
            val callId = getCallId()
            try {
                val dokdistRequest = call.receive<DokdistRequest>()

                val token = getBearerHeader()
                    ?: throw IllegalArgumentException("No Authorization header supplied")

                apiConsumerAccessService.validateConsumerApplicationAZP(
                    authorizedApplicationNameList = authorizedApplicationNameList,
                    token = token,
                )

                val dokdistResponse =
                    dokdistClient.distribuerJournalpost(dokdistRequest, callId)
                        ?: throw RuntimeException("Failed to distribute journalpost: missing response")
                call.respond(dokdistResponse)
            } catch (ex: Exception) {
                handleProxyError(
                    ex = ex,
                    proxyServiceName = "Dokdist",
                )
            }
        }
    }
}
