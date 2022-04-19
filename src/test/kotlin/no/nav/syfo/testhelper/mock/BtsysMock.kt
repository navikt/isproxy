package no.nav.syfo.testhelper.mock

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.btsys.client.BtsysClient
import no.nav.syfo.testhelper.getRandomPort

fun generateResponse(value: Boolean) =
    BtsysClient.Suspendert(value)

class BtsysMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val name = "btsys"
    val server = embeddedServer(
        factory = Netty,
        port = port,
    ) {
        installContentNegotiation()
        routing {
            get(BtsysClient.BTSYS_PATH) {
                call.respond(generateResponse(false))
            }
        }
    }
}
