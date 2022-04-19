package no.nav.syfo.testhelper.mock

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.client.sts.StsClient
import no.nav.syfo.testhelper.getRandomPort

class STSMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val token = StsClient.Token(
        access_token = "test-token",
        token_type = "test-token-type",
        expires_in = 3600,
    )

    val name = "sts"
    val server = embeddedServer(
        factory = Netty,
        port = port,
    ) {
        installContentNegotiation()
        routing {
            get("/rest/v1/sts/token") {
                call.respond(token)
            }
        }
    }
}
