package no.nav.syfo.ereg.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.ereg.client.EregClient.Companion.EREG_PATH
import no.nav.syfo.util.getCallId
import no.nav.syfo.util.handleProxyError

const val eregProxyBasePath = "/api/v1/ereg"
const val eregOrgnrParam = "orgnr"

fun Route.registerEregProxyApi(
    eregClient: EregClient,
) {
    route(eregProxyBasePath) {
        get("$EREG_PATH/{$eregOrgnrParam}") {
            val callId = getCallId()
            try {
                val eregOrgnr = call.parameters[eregOrgnrParam]
                    ?: throw IllegalArgumentException("No Orgnr found in path param")

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
