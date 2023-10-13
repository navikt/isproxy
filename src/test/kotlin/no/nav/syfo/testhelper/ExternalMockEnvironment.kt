package no.nav.syfo.testhelper

import no.nav.syfo.application.ApplicationState
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment {
    val applicationState: ApplicationState = testAppState()
    val wellKnown = wellKnownMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()

    val environment = testEnvironment()
}
