package no.nav.syfo.testhelper

import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.PreAuthorizedClient
import no.nav.syfo.util.configuredJacksonMapper
import java.net.ServerSocket

fun testEnvironment(
    azureTokenEndpoint: String = "azureTokenEndpoint",
    axsysUrl: String,
    btsysUrl: String,
    eregUrl: String,
    stsUrl: String,
    stsSamlUrl: String,
    dokdistUrl: String,
    kuhrsarUrl: String,
    norg2Url: String,
    syfosyketilfelleUrl: String,
) = Environment(
    aadAppClient = "isproxy-client-id",
    azureAppWellKnownUrl = "wellknown",
    aadTokenEndpoint = azureTokenEndpoint,
    aadAppSecret = "client-secret",
    azureAppPreAuthorizedApps = configuredJacksonMapper().writeValueAsString(testAzureAppPreAuthorizedApps),
    serviceuserUsername = "user",
    serviceuserPassword = "password",
    axsysUrl = axsysUrl,
    btsysUrl = btsysUrl,
    eregUrl = eregUrl,
    stsUrl = stsUrl,
    stsSamlUrl = stsSamlUrl,
    dokdistUrl = dokdistUrl,
    norg2Url = norg2Url,
    syfosyketilfelleUrl = syfosyketilfelleUrl,
    fastlegeUrl = "dummyUrl",
    adresseregisterUrl = "dummyUrl",
    kuhrsarClientId = "kuhrsar",
    kuhrsarUrl = kuhrsarUrl,
    subscriptionEndpointURL = "subscriptionUrl",
)

fun testAppState() = ApplicationState(
    alive = true,
    ready = true
)

fun getRandomPort() = ServerSocket(0).use {
    it.localPort
}

const val testFastlegerestClientId = "fastlegerest-client-id"
const val testIsdialogmoteClientId = "isdialogmote-client-id"
const val testIsnarmestelederClientId = "isnarmesteleder-client-id"
const val testPadm2ClientId = "padm2-client-id"
const val testSyfobehandlendeenhetClientId = "syfobehandlendeenhet-client-id"
const val testSyfoveilederClientId = "syfoveileder-client-id"

val testAzureAppPreAuthorizedApps = listOf(
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:isdialogmote",
        clientId = testIsdialogmoteClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:isnarmesteleder",
        clientId = testIsnarmestelederClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:padm2",
        clientId = testPadm2ClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:syfobehandlendeenhet",
        clientId = testSyfobehandlendeenhetClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:syfoveileder",
        clientId = testSyfoveilederClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:fastlegerest",
        clientId = testFastlegerestClientId,
    ),
)
