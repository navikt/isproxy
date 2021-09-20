package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.ereg.client.*
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
    val server = mockEregServer(
        port
    )

    private fun mockEregServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                get("${EregClient.EREG_PATH}/$EregResponseOrgNr") {
                    call.respond(eregResponse)
                }
            }
        }
    }
}
