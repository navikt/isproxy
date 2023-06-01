package no.nav.syfo.testhelper

import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment() {
    val applicationState: ApplicationState = testAppState()
    val wellKnown = wellKnownMock()
    val azureAdMock = AzureAdMock()
    val btsysMock = BtsysMock()
    val stsMock = STSMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()

    val externalApplicationMockMap = hashMapOf(
        azureAdMock.name to azureAdMock.server,
        btsysMock.name to btsysMock.server,
        stsMock.name to stsMock.server,
    )

    val environment = testEnvironment(
        azureTokenEndpoint = azureAdMock.url,
        btsysUrl = btsysMock.url,
        stsUrl = stsMock.url,
        stsSamlUrl = stsMock.url,
    )
}

fun ExternalMockEnvironment.startExternalMocks() {
    this.externalApplicationMockMap.start()
}

fun ExternalMockEnvironment.stopExternalMocks() {
    this.externalApplicationMockMap.stop()
}

fun HashMap<String, NettyApplicationEngine>.start() {
    this.forEach {
        it.value.start()
    }
}

fun HashMap<String, NettyApplicationEngine>.stop(
    gracePeriodMillis: Long = 1L,
    timeoutMillis: Long = 10L,
) {
    this.forEach {
        it.value.stop(gracePeriodMillis, timeoutMillis)
    }
}
