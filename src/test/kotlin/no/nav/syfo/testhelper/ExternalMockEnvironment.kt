package no.nav.syfo.testhelper

import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment() {
    val applicationState: ApplicationState = testAppState()
    val wellKnown = wellKnownMock()
    val eregMock = EregMock()
    val stsMock = STSMock()
    val dokDistMock = DokDistMock()

    val externalApplicationMockMap = hashMapOf(
        eregMock.name to eregMock.server,
        stsMock.name to stsMock.server,
        dokDistMock.name to dokDistMock.server,
    )

    val environment = testEnvironment(
        eregUrl = eregMock.url,
        stsUrl = stsMock.url,
        dokdistUrl = dokDistMock.url,
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
