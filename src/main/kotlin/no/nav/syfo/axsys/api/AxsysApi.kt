package no.nav.syfo.axsys.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.axsys.client.AxsysClient
import no.nav.syfo.util.getCallId
import no.nav.syfo.util.proxyRequestHandler

const val axsysProxyBasePath = "/api/v1/axsys"
const val axsysProxyVeiledereEnhetBrukerePath = "/veiledere/enhet"
const val axsysEnhetNrParam = "enhetNr"

fun Route.registerAxsysApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    axsysClient: AxsysClient,
) {
    route(axsysProxyBasePath) {
        get("$axsysProxyVeiledereEnhetBrukerePath/{$axsysEnhetNrParam}") {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Axsys",
            ) {
                val enhetNr = call.parameters[axsysEnhetNrParam]
                    ?: throw IllegalArgumentException("No AktorId found in path param")

                val axsysVeilederList = axsysClient.veiledere(
                    callId = getCallId(),
                    enhetNr = enhetNr,
                )
                call.respond(axsysVeilederList)
            }
        }
    }
}
