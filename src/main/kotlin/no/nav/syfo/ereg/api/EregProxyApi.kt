package no.nav.syfo.ereg.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.util.*

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
            val callId = getCallId()
            try {
                val eregOrgnr = call.parameters[eregOrgnrParam]
                    ?: throw IllegalArgumentException("No Orgnr found in path param")

                val token = getBearerHeader()
                    ?: throw IllegalArgumentException("No Authorization header supplied")

                apiConsumerAccessService.validateConsumerApplicationAZP(
                    authorizedApplicationNameList = authorizedApplicationNameList,
                    token = token,
                )

                val eregOrganisasjonResponse = eregClient.organisasjon(
                    callId = callId,
                    orgNr = eregOrgnr,
                )
                call.respond(eregOrganisasjonResponse)
            } catch (ex: Exception) {
                handleProxyError(
                    ex = ex,
                    proxyServiceName = "Ereg",
                )
            }
        }
    }
}
