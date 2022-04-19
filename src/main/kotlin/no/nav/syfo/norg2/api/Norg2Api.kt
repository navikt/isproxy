package no.nav.syfo.norg2.api

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.norg2.client.Norg2Client
import no.nav.syfo.norg2.domain.ArbeidsfordelingCriteria
import no.nav.syfo.util.proxyRequestHandler

const val norg2ProxyBasePath = "/api/v1/norg2"
const val norg2ProxyArbeidsfordelingBestmatchPath = "/arbeidsfordeling/enheter/bestmatch"

fun Route.registerNorg2Api(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    norg2Client: Norg2Client,
) {
    route(norg2ProxyBasePath) {
        post(norg2ProxyArbeidsfordelingBestmatchPath) {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Norg2",
            ) {
                val arbeidsfordelingCriteria: ArbeidsfordelingCriteria = call.receive()

                val arbeidsfordelingResponse = norg2Client.arbeidsfordeling(
                    arbeidsfordelingCriteria = arbeidsfordelingCriteria,
                )
                call.respond(arbeidsfordelingResponse)
            }
        }
    }
}
