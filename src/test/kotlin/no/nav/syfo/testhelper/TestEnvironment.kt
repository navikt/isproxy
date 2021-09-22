package no.nav.syfo.testhelper

import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.PreAuthorizedClient
import no.nav.syfo.util.configuredJacksonMapper
import java.net.ServerSocket

fun testEnvironment(
    eregUrl: String,
    stsUrl: String,
    dokdistUrl: String,
) = Environment(
    aadAppClient = "isproxy-client-id",
    azureAppWellKnownUrl = "wellknown",
    azureAppPreAuthorizedApps = configuredJacksonMapper().writeValueAsString(testAzureAppPreAuthorizedApps),
    serviceuserUsername = "user",
    serviceuserPassword = "password",
    eregUrl = eregUrl,
    stsUrl = stsUrl,
    dokdistUrl = dokdistUrl,
)

fun testAppState() = ApplicationState(
    alive = true,
    ready = true
)

fun getRandomPort() = ServerSocket(0).use {
    it.localPort
}

const val testIsdialogmoteClientId = "isdialogmote-client-id"
const val testIsnarmestelederClientId = "isnarmesteleder-client-id"

val testAzureAppPreAuthorizedApps = listOf(
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:isdialogmote",
        clientId = testIsdialogmoteClientId,
    ),
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:isnarmesteleder",
        clientId = testIsnarmestelederClientId,
    ),
)
