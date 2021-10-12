package no.nav.syfo.syfosyketilfelle.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.domain.AktorId
import no.nav.syfo.domain.Virksomhetsnummer
import no.nav.syfo.syfosyketilfelle.client.SyfosyketilfelleClient
import no.nav.syfo.util.proxyRequestHandler

const val syfosyktilfelleProxyBasePath = "/api/v1/syfosyketilfelle"
const val syfosyketilfelleProxyOppfolgingstilfellePersonPath = "/oppfolgingstilfelle/person"
const val syfosyketilfelleProxyOppfolgingstilfellePersonUtenArbeidsgiverPath = "/utenarbeidsgiver"
const val syfosyketilfelleAktorIdParam = "aktorid"
const val syfosyketilfelleVirksomhetsnummerParam = "virksomhetsnummer"

fun Route.registerSyfosyketilfelleApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    syfosyketilfelleClient: SyfosyketilfelleClient,
) {
    route(syfosyktilfelleProxyBasePath) {
        get("$syfosyketilfelleProxyOppfolgingstilfellePersonPath/{$syfosyketilfelleAktorIdParam}") {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Syfosyketilfelle",
            ) {
                val aktorId = call.parameters[syfosyketilfelleAktorIdParam]?.let { aktorId ->
                    AktorId(aktorId)
                } ?: throw IllegalArgumentException("No AktorId found in path param")

                syfosyketilfelleClient.oppfolgingstilfellePerson(
                    aktorId = aktorId,
                )?.let { kOppfolgingstilfellePerson ->
                    call.respond(kOppfolgingstilfellePerson)
                } ?: call.respond(HttpStatusCode.NoContent)
            }
        }

        get("$syfosyketilfelleProxyOppfolgingstilfellePersonPath/{$syfosyketilfelleAktorIdParam}$syfosyketilfelleProxyOppfolgingstilfellePersonUtenArbeidsgiverPath") {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Syfosyketilfelle",
            ) {
                val aktorId = call.parameters[syfosyketilfelleAktorIdParam]?.let { aktorId ->
                    AktorId(aktorId)
                } ?: throw IllegalArgumentException("No AktorId found in path param")

                syfosyketilfelleClient.oppfolgingstilfellePersonUtenArbeidsgiver(
                    aktorId = aktorId,
                )?.let { kOppfolgingstilfellePerson ->
                    call.respond(kOppfolgingstilfellePerson)
                } ?: call.respond(HttpStatusCode.NoContent)
            }
        }

        get("$syfosyketilfelleProxyOppfolgingstilfellePersonPath/{$syfosyketilfelleAktorIdParam}/{$syfosyketilfelleVirksomhetsnummerParam}") {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Syfosyketilfelle",
            ) {
                val aktorId = call.parameters[syfosyketilfelleAktorIdParam]?.let { aktorId ->
                    AktorId(aktorId)
                } ?: throw IllegalArgumentException("No AktorId found in path param")

                val virksomhetsnummer =
                    call.parameters[syfosyketilfelleVirksomhetsnummerParam]?.let { virksomhetsnummer ->
                        Virksomhetsnummer(virksomhetsnummer)
                    } ?: throw IllegalArgumentException("No Virksomhetsnummer found in path param")

                syfosyketilfelleClient.oppfolgingstilfellePersonVirksomhet(
                    aktorId = aktorId,
                    virksomhetsnummer = virksomhetsnummer,
                )?.let { kOppfolgingstilfellePerson ->
                    call.respond(kOppfolgingstilfellePerson)
                } ?: call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
