package no.nav.syfo.testhelper

import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import java.net.ServerSocket

fun testEnvironment(
    eregUrl: String,
    stsUrl: String,
    dokdistUrl: String,
) = Environment(
    aadAppClient = "isproxy-client-id",
    azureAppWellKnownUrl = "wellknown",
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
