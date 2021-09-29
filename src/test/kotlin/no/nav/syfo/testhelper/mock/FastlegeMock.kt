package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.testhelper.getRandomPort

class FastlegeMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val name = "fastlege"
    val server = mockFastlegeServer(
        port
    )

    private fun mockFastlegeServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                get() {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
