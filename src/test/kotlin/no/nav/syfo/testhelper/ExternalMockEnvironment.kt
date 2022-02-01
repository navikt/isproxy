package no.nav.syfo.testhelper

import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment() {
    val applicationState: ApplicationState = testAppState()
    val wellKnown = wellKnownMock()
    val axsysMock = AxsysMock()
    val eregMock = EregMock()
    val btsysMock = BtsysMock()
    val stsMock = STSMock()
    val dokDistMock = DokDistMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()
    val norg2Mock = Norg2Mock()
    val syfosyketilfelleMock = SyfosyketilfelleMock()
    val subscriptionMock = SubscriptionMock()

    val externalApplicationMockMap = hashMapOf(
        axsysMock.name to axsysMock.server,
        btsysMock.name to btsysMock.server,
        eregMock.name to eregMock.server,
        stsMock.name to stsMock.server,
        dokDistMock.name to dokDistMock.server,
        norg2Mock.name to norg2Mock.server,
        syfosyketilfelleMock.name to syfosyketilfelleMock.server,
    )

    val environment = testEnvironment(
        axsysUrl = axsysMock.url,
        btsysUrl = btsysMock.url,
        eregUrl = eregMock.url,
        stsUrl = stsMock.url,
        stsSamlUrl = stsMock.url,
        dokdistUrl = dokDistMock.url,
        norg2Url = norg2Mock.url,
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
