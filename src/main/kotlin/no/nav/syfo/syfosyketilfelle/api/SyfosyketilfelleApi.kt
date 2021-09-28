package no.nav.syfo.syfosyketilfelle.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.domain.AktorId
import no.nav.syfo.syfosyketilfelle.client.SyfosyketilfelleClient
import no.nav.syfo.util.*

const val syfosyktilfelleProxyBasePath = "/api/v1/syfosyketilfelle"
const val syfosyketilfelleProxyOppfolgingstilfellePersonPath = "/oppfolgingstilfelle/person"
const val syfosyketilfelleAktorIdParam = "aktorid"

fun Route.registerSyfosyketilfelleApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    syfosyketilfelleClient: SyfosyketilfelleClient,
) {
    route(syfosyktilfelleProxyBasePath) {
        get("$syfosyketilfelleProxyOppfolgingstilfellePersonPath/{$syfosyketilfelleAktorIdParam}") {
            val callId = getCallId()
            try {
                val aktorId = call.parameters[syfosyketilfelleAktorIdParam]?.let { aktorId ->
                    AktorId(aktorId)
                } ?: throw IllegalArgumentException("No AktorId found in path param")

                val token = getBearerHeader()
                    ?: throw IllegalArgumentException("No Authorization header supplied")

                apiConsumerAccessService.validateConsumerApplicationAZP(
                    authorizedApplicationNameList = authorizedApplicationNameList,
                    token = token,
                )

                syfosyketilfelleClient.oppfolgingstilfellePerson(
                    callId = callId,
                    aktorId = aktorId,
                )?.let { kOppfolgingstilfellePerson ->
                    call.respond(kOppfolgingstilfellePerson)
                } ?: call.respond(HttpStatusCode.NoContent)
            } catch (ex: Exception) {
                handleProxyError(
                    ex = ex,
                    proxyServiceName = "Syfosyketilfelle",
                )
            }
        }
    }
}
