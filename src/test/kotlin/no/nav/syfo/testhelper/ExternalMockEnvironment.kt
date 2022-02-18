package no.nav.syfo.testhelper

import io.ktor.server.netty.*
import io.mockk.mockk
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment() {
    val applicationState: ApplicationState = testAppState()
    val wellKnown = wellKnownMock()
    val azureAdMock = AzureAdMock()
    val axsysMock = AxsysMock()
    val eregMock = EregMock()
    val btsysMock = BtsysMock()
    val stsMock = STSMock()
    val dokDistMock = DokDistMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()
    val kuhrsarMock = KuhrsarMock()
    val norg2Mock = Norg2Mock()
    val syfosyketilfelleMock = SyfosyketilfelleMock()
    val subscriptionMock = mockk<SubscriptionPort>()

    val externalApplicationMockMap = hashMapOf(
        azureAdMock.name to azureAdMock.server,
        axsysMock.name to axsysMock.server,
        btsysMock.name to btsysMock.server,
        eregMock.name to eregMock.server,
        stsMock.name to stsMock.server,
        dokDistMock.name to dokDistMock.server,
        kuhrsarMock.name to kuhrsarMock.server,
        norg2Mock.name to norg2Mock.server,
        syfosyketilfelleMock.name to syfosyketilfelleMock.server,
    )

    val environment = testEnvironment(
        azureTokenEndpoint = azureAdMock.url,
        axsysUrl = axsysMock.url,
        btsysUrl = btsysMock.url,
        eregUrl = eregMock.url,
        stsUrl = stsMock.url,
        stsSamlUrl = stsMock.url,
        dokdistUrl = dokDistMock.url,
        kuhrsarUrl = kuhrsarMock.url,
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
