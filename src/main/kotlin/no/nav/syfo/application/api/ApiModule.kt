package no.nav.syfo.application.api

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.dokdist.client.DokdistClient
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.adresseregister.adresseregisterSoapClient
import no.nav.syfo.dokdist.api.registerDokdistApi
import no.nav.syfo.fastlege.ws.fastlege.FastlegeInformasjonClient
import no.nav.syfo.fastlege.ws.fastlege.fastlegeSoapClient
import no.nav.syfo.fastlege.api.registerFastlegeApi
import no.nav.syfo.ereg.api.registerEregProxyApi
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.fastlege.api.registerFastlegepraksisApi
import no.nav.syfo.syfosyketilfelle.api.registerSyfosyketilfelleApi
import no.nav.syfo.syfosyketilfelle.client.SyfosyketilfelleClient

fun Application.apiModule(
    applicationState: ApplicationState,
    environment: Environment,
    wellKnown: WellKnown,
) {
    installMetrics()
    installCallId()
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

    val stsClientProperties = StsClientProperties(
        baseUrl = environment.stsUrl,
        serviceuserUsername = environment.serviceuserUsername,
        serviceuserPassword = environment.serviceuserPassword,
    )
    val stsClient = StsClient(
        properties = stsClientProperties,
    )
    val dokdistClient = DokdistClient(
        dokdistBaseUrl = environment.dokdistUrl,
        stsClient = stsClient,
    )
    val eregClient = EregClient(
        baseUrl = environment.eregUrl,
        stsClient = stsClient,
    )
    val syfosyketilfelleClient = SyfosyketilfelleClient(
        baseUrl = environment.syfosyketilfelleUrl,
        stsClient = stsClient,
    )
    val fastlegeInformasjonClient = FastlegeInformasjonClient(
        fastlegeSoapClient = fastlegeSoapClient(
            serviceUrl = environment.fastlegeUrl,
            stsProperties = stsClientProperties,
        )
    )
    val adresseregisterClient = AdresseregisterClient(
        adresseregisterSoapClient = adresseregisterSoapClient(
            serviceUrl = environment.adresseregisterUrl,
            stsProperties = stsClientProperties,
        )
    )

    val apiConsumerAccessService = APIConsumerAccessService(
        azureAppPreAuthorizedApps = environment.azureAppPreAuthorizedApps,
    )

    routing {
        registerPodApi(applicationState)
        registerPrometheusApi()

        authenticate(JwtIssuerType.AZUREAD_V2.name) {
            registerDokdistApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.dokdistAPIAuthorizedConsumerApplicationNameList,
                dokdistClient = dokdistClient,
            )
            registerFastlegeApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.fastlegeAPIAuthorizedConsumerApplicationNameList,
                fastlegeClient = fastlegeInformasjonClient,
            )
            registerFastlegepraksisApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.fastlegepraksisAPIAuthorizedConsumerApplicationNameList,
                adresseregisterClient = adresseregisterClient,
            )
            registerEregProxyApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.eregAPIAuthorizedConsumerApplicationNameList,
                eregClient = eregClient,
            )
            registerSyfosyketilfelleApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.syfosyketilfelleAPIAuthorizedConsumerApplicationNameList,
                syfosyketilfelleClient = syfosyketilfelleClient,
            )
        }
    }
}
