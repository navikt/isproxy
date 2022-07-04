package no.nav.syfo.btsys.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.btsys.client.BtsysClient
import no.nav.syfo.util.*

const val btsysProxyBasePath = "/api/v1/btsys"
const val btsysProxySuspendertPath = "/suspensjon/status"

fun Route.registerBtsysProxyApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    btsysClient: BtsysClient,
) {
    route(btsysProxyBasePath) {
        get(btsysProxySuspendertPath) {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Btsys",
            ) {
                val personIdent = getHeader(NAV_PERSONIDENT_HEADER)
                    ?: throw RuntimeException("Btsys proxy: PersonIdent missing in request")
                val oppslagsdato = call.parameters["oppslagsdato"]
                    ?: throw RuntimeException("Btsys proxy: oppslagsdato param missing in request")
                val suspendert = btsysClient.suspendert(personIdent, oppslagsdato, getCallId())
                call.respond(suspendert)
            }
        }
    }
}
