package no.nav.syfo.testhelper.mock

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.ereg.domain.EregOrganisasjonNavn
import no.nav.syfo.ereg.domain.EregOrganisasjonResponse
import no.nav.syfo.testhelper.getRandomPort

const val EregResponseOrgNr = "912345678"

fun generateEregOrganisasjonResponse() =
    EregOrganisasjonResponse(
        navn = EregOrganisasjonNavn(
            navnelinje1 = "Kristians Test AS",
            redigertnavn = "Kristians Test AS, Oslo"
        )
    )

class EregMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val eregResponse = generateEregOrganisasjonResponse()

    val name = "ereg"
    val server = embeddedServer(
        factory = Netty,
        port = port,
    ) {
        installContentNegotiation()
        routing {
            get("${EregClient.EREG_PATH}/$EregResponseOrgNr") {
                call.respond(eregResponse)
            }
        }
    }
}
