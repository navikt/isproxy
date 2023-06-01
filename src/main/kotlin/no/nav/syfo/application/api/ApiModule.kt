package no.nav.syfo.application.api

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.btsys.api.registerBtsysProxyApi
import no.nav.syfo.btsys.client.BtsysClient
import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.client.azuread.AzureAdClient
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.fastlege.api.registerFastlegeApi
import no.nav.syfo.fastlege.api.registerFastlegepraksisApi
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.fastlege.FastlegeInformasjonClient
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.schemas.reg.flr.IFlrReadOperations

fun Application.apiModule(
    applicationState: ApplicationState,
    environment: Environment,
    wellKnown: WellKnown,
    fastlegeSoapClient: IFlrReadOperations,
    adresseregisterSoapClient: ICommunicationPartyService,
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
    val azureAdClient = AzureAdClient(
        aadAppClient = environment.aadAppClient,
        aadAppSecret = environment.aadAppSecret,
        aadTokenEndpoint = environment.aadTokenEndpoint,
    )
    val btsysClient = BtsysClient(
        baseUrl = environment.btsysUrl,
        stsClient = stsClient,
        serviceUserName = environment.serviceuserUsername,
    )
    val fastlegeInformasjonClient = FastlegeInformasjonClient(
        fastlegeSoapClient = fastlegeSoapClient,
    )
    val adresseregisterClient = AdresseregisterClient(
        adresseregisterSoapClient = adresseregisterSoapClient,
    )

    val apiConsumerAccessService = APIConsumerAccessService(
        azureAppPreAuthorizedApps = environment.azureAppPreAuthorizedApps,
    )

    routing {
        registerPodApi(applicationState)
        registerPrometheusApi()

        authenticate(JwtIssuerType.AZUREAD_V2.name) {
            registerBtsysProxyApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.btsysAPIAuthorizedConsumerApplicationNameList,
                btsysClient = btsysClient,
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
        }
    }
}
