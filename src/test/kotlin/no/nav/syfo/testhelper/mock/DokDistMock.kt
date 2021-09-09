package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.client.dokdist.DokdistClient
import no.nav.syfo.client.dokdist.DokdistResponse
import no.nav.syfo.testhelper.getRandomPort

class DokDistMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val name = "dokdist"
    val server = mockDokDistServer(
        port
    )

    private fun mockDokDistServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                post(DokdistClient.DISTRIBUER_JOURNALPOST_PATH) {
                    call.respond(DokdistResponse("bestillings_id_fra_dokdist"))
                }
            }
        }
    }
}
