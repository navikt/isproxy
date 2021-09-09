package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
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
    val server = mockSTSServer(
        port
    )

    private fun mockSTSServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                get("/rest/v1/sts/token") {
                    call.respond(token)
                }
            }
        }
    }
}
