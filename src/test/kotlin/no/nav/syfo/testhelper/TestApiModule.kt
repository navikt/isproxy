package no.nav.syfo.testhelper

import io.ktor.server.application.*
import no.nav.syfo.application.api.apiModule

fun Application.testApiModule(
    externalMockEnvironment: ExternalMockEnvironment,
) {
    this.apiModule(
        applicationState = externalMockEnvironment.applicationState,
        environment = externalMockEnvironment.environment,
        wellKnown = externalMockEnvironment.wellKnown,
        fastlegeSoapClient = externalMockEnvironment.fastlegeMock,
        adresseregisterSoapClient = externalMockEnvironment.adresseregisterMock,
    )
}
