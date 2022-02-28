package no.nav.syfo.application.api

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.axsys.api.registerAxsysApi
import no.nav.syfo.axsys.client.AxsysClient
import no.nav.syfo.btsys.client.BtsysClient
import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.client.azuread.v2.AzureAdClient
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.ereg.api.*
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.fastlege.api.registerFastlegeApi
import no.nav.syfo.fastlege.api.registerFastlegepraksisApi
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.fastlege.FastlegeInformasjonClient
import no.nav.syfo.kuhrsar.client.KuhrSarClient
import no.nav.syfo.norg2.api.registerNorg2Api
import no.nav.syfo.norg2.client.Norg2Client
import no.nav.syfo.syfosyketilfelle.api.registerSyfosyketilfelleApi
import no.nav.syfo.syfosyketilfelle.client.SyfosyketilfelleClient
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.schemas.reg.flr.IFlrReadOperations

fun Application.apiModule(
    applicationState: ApplicationState,
    environment: Environment,
    wellKnown: WellKnown,
    fastlegeSoapClient: IFlrReadOperations,
    adresseregisterSoapClient: ICommunicationPartyService,
    subscriptionPort: SubscriptionPort,
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
    val axsysClient = AxsysClient(
        baseUrl = environment.axsysUrl,
    )
    val eregClient = EregClient(
        baseUrl = environment.eregUrl,
        stsClient = stsClient,
    )
    val btsysClient = BtsysClient(
        baseUrl = environment.btsysUrl,
        stsClient = stsClient,
        serviceUserName = environment.serviceuserUsername,
    )
    val kuhrsarClient = KuhrSarClient(
        azureAdClient = azureAdClient,
        kuhrsarClientId = environment.kuhrsarClientId,
        kuhrsarUrl = environment.kuhrsarUrl,
    )
    val norg2Client = Norg2Client(
        baseUrl = environment.norg2Url,
    )
    val syfosyketilfelleClient = SyfosyketilfelleClient(
        baseUrl = environment.syfosyketilfelleUrl,
        stsClient = stsClient,
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
            registerAxsysApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.axsysAPIAuthorizedConsumerApplicationNameList,
                axsysClient = axsysClient,
            )
            registerBtsysProxyApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.btsysAPIAuthorizedConsumerApplicationNameList,
                btsysClient = btsysClient,
            )
            registerEregProxyApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.eregAPIAuthorizedConsumerApplicationNameList,
                eregClient = eregClient,
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
            registerKuhrsarApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.kuhrsarAPIAuthorizedConsumerApplicationNameList,
                kuhrsarClient = kuhrsarClient,
                subscriptionPort = subscriptionPort,
            )
            registerNorg2Api(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.norg2APIAuthorizedConsumerApplicationNameList,
                norg2Client = norg2Client,
            )
            registerSyfosyketilfelleApi(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = environment.syfosyketilfelleAPIAuthorizedConsumerApplicationNameList,
                syfosyketilfelleClient = syfosyketilfelleClient,
            )
        }
    }
}
