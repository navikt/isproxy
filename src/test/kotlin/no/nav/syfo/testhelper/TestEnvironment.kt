package no.nav.syfo.testhelper

import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.PreAuthorizedClient
import no.nav.syfo.util.configuredJacksonMapper
import java.net.ServerSocket

fun testEnvironment(
    azureTokenEndpoint: String = "azureTokenEndpoint",
    btsysUrl: String,
    stsUrl: String,
    stsSamlUrl: String,
    kuhrsarUrl: String,
) = Environment(
    aadAppClient = "isproxy-client-id",
    azureAppWellKnownUrl = "wellknown",
    aadTokenEndpoint = azureTokenEndpoint,
    aadAppSecret = "client-secret",
    azureAppPreAuthorizedApps = configuredJacksonMapper().writeValueAsString(testAzureAppPreAuthorizedApps),
    serviceuserUsername = "user",
    serviceuserPassword = "password",
    btsysUrl = btsysUrl,
    stsUrl = stsUrl,
    stsSamlUrl = stsSamlUrl,
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
const val testPadm2ClientId = "padm2-client-id"

val testAzureAppPreAuthorizedApps = listOf(
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:padm2",
        clientId = testPadm2ClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:fastlegerest",
        clientId = testFastlegerestClientId,
    ),
)
