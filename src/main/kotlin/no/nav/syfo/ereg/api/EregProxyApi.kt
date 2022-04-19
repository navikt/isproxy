package no.nav.syfo.ereg.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.util.proxyRequestHandler

const val eregProxyBasePath = "/api/v1/ereg"
const val eregProxyOrganisasjonPath = "/organisasjon"
const val eregOrgnrParam = "orgnr"

fun Route.registerEregProxyApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    eregClient: EregClient,
) {
    route(eregProxyBasePath) {
        get("$eregProxyOrganisasjonPath/{$eregOrgnrParam}") {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Ereg",
            ) {
                val eregOrgnr = call.parameters[eregOrgnrParam]
                    ?: throw IllegalArgumentException("No Orgnr found in path param")

                val eregOrganisasjonResponse = eregClient.organisasjon(
                    orgNr = eregOrgnr,
                )
                call.respond(eregOrganisasjonResponse)
            }
        }
    }
}
