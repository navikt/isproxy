package no.nav.syfo.application.api

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.client.dokdist.DokdistClient
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.dokdist.api.registerDokdistApi
import no.nav.syfo.ereg.api.registerEregProxyApi
import no.nav.syfo.ereg.client.EregClient

fun Application.apiModule(
    applicationState: ApplicationState,
    environment: Environment,
    wellKnown: WellKnown,
) {
    installMetrics()
    installContentNegotiation()
    installStatusPages()
    installJwtAuthentication(
        jwtIssuerList = listOf(
            JwtIssuer(
                acceptedAudienceList = listOf(environment.aadAppClient),
                jwtIssuerType = JwtIssuerType.AZUREAD_V2,
                wellKnown = wellKnown,
            ),
        ),
    )

    val stsClient = StsClient(
        baseUrl = environment.stsUrl,
        serviceuserUsername = environment.serviceuserUsername,
        serviceuserPassword = environment.serviceuserPassword,
    )

    val dokdistClient = DokdistClient(
        dokdistBaseUrl = environment.dokdistUrl,
        stsClient = stsClient,
    )
    val eregClient = EregClient(
        baseUrl = environment.eregUrl,
        stsClient = stsClient,
    )

    val apiConsumerAccessService = APIConsumerAccessService(
        azureAppPreAuthorizedApps = environment.azureAppPreAuthorizedApps,
    )

    routing {
        registerPodApi(applicationState)
        registerPrometheusApi()

        authenticate(JwtIssuerType.AZUREAD_V2.name) {
            registerDokdistApi(
                dokdistClient = dokdistClient
            )
            registerEregProxyApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.eregAPIAuthorizedConsumerApplicationNameList,
                eregClient = eregClient,
            )
        }
    }
}
