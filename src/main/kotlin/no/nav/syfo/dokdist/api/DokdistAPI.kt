package no.nav.syfo.dokdist.api

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.dokdist.client.DokdistClient
import no.nav.syfo.dokdist.domain.DokdistRequest
import no.nav.syfo.util.proxyRequestHandler

const val dokDistBasePath = "/api/v1/dokdist"
const val distribuerJournalpostPath = "/distribuerjournalpost"

fun Route.registerDokdistApi(
    dokdistClient: DokdistClient,
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>
) {
    route(dokDistBasePath) {
        post(distribuerJournalpostPath) {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Dokdist",
            ) {
                val dokdistRequest = call.receive<DokdistRequest>()

                val dokdistResponse = dokdistClient.distribuerJournalpost(
                    dokdistRequest = dokdistRequest,
                )
                call.respond(dokdistResponse)
            }
        }
    }
}
