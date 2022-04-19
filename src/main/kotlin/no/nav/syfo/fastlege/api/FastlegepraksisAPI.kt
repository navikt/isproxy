package no.nav.syfo.fastlege.api

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.util.*

const val fastlegepraksisBasePath = "/api/v1/fastlegepraksis"
const val herid = "herid"

fun Route.registerFastlegepraksisApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    adresseregisterClient: AdresseregisterClient,
) {
    route(fastlegepraksisBasePath) {
        get("/{$herid}") {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Fastlegepraksis",
            ) {
                val herId = call.parameters[herid]?.toInt()
                    ?: throw IllegalArgumentException("No herId found in path param")

                adresseregisterClient.hentPraksisInfoForFastlege(
                    herId = herId
                )?.let { praksisInfo ->
                    call.respond(praksisInfo)
                } ?: call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
