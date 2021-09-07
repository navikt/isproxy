package no.nav.syfo.application.api

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import no.nav.syfo.api.registerProxyApi
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.client.dokdist.DokdistClient
import no.nav.syfo.client.sts.StsClient

fun Application.apiModule(
    applicationState: ApplicationState,
    environment: Environment,
) {
    installMetrics()
    installContentNegotiation()
    installStatusPages()
    installJwtAuthentication(
        jwtIssuerList = listOf(
            JwtIssuer(
                acceptedAudienceList = listOf(environment.aadAppClient),
                jwtIssuerType = JwtIssuerType.AZUREAD_V2,
                wellKnown = getWellKnown(environment.azureAppWellKnownUrl),
            ),
        ),
    )

    val stsClient = StsClient(
        baseUrl = environment.stsUrl,
        serviceuserUsername = environment.serviceuserUsername,
        serviceuserPassword = environment.serviceuserPassword,
    )

    routing {
        registerPodApi(applicationState)
        registerPrometheusApi()

        authenticate(JwtIssuerType.AZUREAD_V2.name) {
            registerProxyApi(
                dokdistClient = DokdistClient(
                    dokdistBaseUrl = environment.dokdistUrl,
                    stsClient = stsClient,
                )
            )
        }
    }
}
