package no.nav.syfo.dkif.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.dkif.client.DkifClient
import no.nav.syfo.dkif.client.DkifClient.Companion.NAV_PERSONIDENTER_HEADER
import no.nav.syfo.util.*

const val dkifProxyBasePath = "/api/v1/dkif"
const val dkifProxyKontaktinformasjonPath = "/kontaktinformasjon"

fun Route.registerDkifApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    dkifClient: DkifClient,
) {
    route(dkifProxyBasePath) {
        get(dkifProxyKontaktinformasjonPath) {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Dkif",
            ) {
                val personIdentNumberList = getHeader(NAV_PERSONIDENTER_HEADER)
                    ?: throw IllegalArgumentException("No personIdentNumberList supplied in header")

                val dkifKontaktinformasjonResponse = dkifClient.digitalKontaktinfoBolk(
                    callId = getCallId(),
                    personIdentNumberList = personIdentNumberList,
                )
                call.respond(dkifKontaktinformasjonResponse)
            }
        }
    }
}
