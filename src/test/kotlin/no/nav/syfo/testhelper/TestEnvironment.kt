package no.nav.syfo.testhelper

import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.access.PreAuthorizedClient
import no.nav.syfo.util.configuredJacksonMapper

fun testEnvironment() = Environment(
    aadAppClient = "isproxy-client-id",
    azureAppWellKnownUrl = "wellknown",
    azureAppPreAuthorizedApps = configuredJacksonMapper().writeValueAsString(testAzureAppPreAuthorizedApps),
    serviceuserUsername = "user",
    serviceuserPassword = "password",
    stsSamlUrl = "stsSamlUrl",
    fastlegeUrl = "dummyUrl",
    adresseregisterUrl = "dummyUrl",
)

fun testAppState() = ApplicationState(
    alive = true,
    ready = true
)

const val testFastlegerestClientId = "fastlegerest-client-id"

val testAzureAppPreAuthorizedApps = listOf(
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:fastlegerest",
        clientId = testFastlegerestClientId,
    ),
)
