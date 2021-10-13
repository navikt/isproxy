package no.nav.syfo.testhelper

import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment() {
    val applicationState: ApplicationState = testAppState()
    val wellKnown = wellKnownMock()
    val axsysMock = AxsysMock()
    val dkifMock = DkifMock()
    val eregMock = EregMock()
    val stsMock = STSMock()
    val dokDistMock = DokDistMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()
    val syfosyketilfelleMock = SyfosyketilfelleMock()

    val externalApplicationMockMap = hashMapOf(
        axsysMock.name to axsysMock.server,
        dkifMock.name to dkifMock.server,
        eregMock.name to eregMock.server,
        stsMock.name to stsMock.server,
        dokDistMock.name to dokDistMock.server,
        syfosyketilfelleMock.name to syfosyketilfelleMock.server,
    )

    val environment = testEnvironment(
        axsysUrl = axsysMock.url,
        dkifUrl = dkifMock.url,
        eregUrl = eregMock.url,
        stsUrl = stsMock.url,
        stsSamlUrl = stsMock.url,
        dokdistUrl = dokDistMock.url,
        syfosyketilfelleUrl = syfosyketilfelleMock.url,
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
