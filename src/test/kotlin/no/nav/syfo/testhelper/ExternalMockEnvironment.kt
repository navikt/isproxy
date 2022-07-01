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
    val eregMock = EregMock()
    val btsysMock = BtsysMock()
    val stsMock = STSMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()
    val kuhrsarMock = KuhrsarMock()
    val norg2Mock = Norg2Mock()
    val subscriptionMock = mockk<SubscriptionPort>()

    val externalApplicationMockMap = hashMapOf(
        azureAdMock.name to azureAdMock.server,
        btsysMock.name to btsysMock.server,
        eregMock.name to eregMock.server,
        stsMock.name to stsMock.server,
        kuhrsarMock.name to kuhrsarMock.server,
        norg2Mock.name to norg2Mock.server,
    )

    val environment = testEnvironment(
        azureTokenEndpoint = azureAdMock.url,
        btsysUrl = btsysMock.url,
        eregUrl = eregMock.url,
        stsUrl = stsMock.url,
        stsSamlUrl = stsMock.url,
        kuhrsarUrl = kuhrsarMock.url,
        norg2Url = norg2Mock.url,
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
