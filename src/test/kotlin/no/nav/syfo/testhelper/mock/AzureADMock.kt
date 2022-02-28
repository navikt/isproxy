package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.WellKnown
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.client.azuread.AzureAdTokenResponse
import no.nav.syfo.testhelper.UserConstants.AZUREAD_TOKEN
import no.nav.syfo.testhelper.getRandomPort
import java.nio.file.Paths

fun wellKnownMock(): WellKnown {
    val path = "src/test/resources/jwkset.json"
    val uri = Paths.get(path).toUri().toURL()
    return WellKnown(
        authorization_endpoint = "authorizationendpoint",
        token_endpoint = "tokenendpoint",
        jwks_uri = uri.toString(),
        issuer = "https://sts.issuer.net"
    )
}

class AzureAdMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val aadTokenResponse = AzureAdTokenResponse(
        access_token = AZUREAD_TOKEN,
        expires_in = 3600,
        token_type = "type"
    )

    val name = "azuread"
    val server = mockAzureAdServer(port = port)

    private fun mockAzureAdServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                post {
                    call.respond(aadTokenResponse)
                }
            }
        }
    }
}
