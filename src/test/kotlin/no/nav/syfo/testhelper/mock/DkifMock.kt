package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.dkif.client.DkifClient
import no.nav.syfo.dkif.domain.DigitalKontaktinfo
import no.nav.syfo.dkif.domain.DigitalKontaktinfoBolk
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT
import no.nav.syfo.testhelper.getRandomPort
import no.nav.syfo.util.getHeader

fun digitalKontaktinfoBolkKanVarslesTrue() = DigitalKontaktinfoBolk(
    kontaktinfo = mapOf(
        ARBEIDSTAKER_PERSONIDENT to DigitalKontaktinfo(
            epostadresse = "epost@nav.no",
            kanVarsles = true,
            reservert = false,
            mobiltelefonnummer = "99119911",
            personident = ARBEIDSTAKER_PERSONIDENT,
        )
    )
)

class DkifMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val dkifResponse = digitalKontaktinfoBolkKanVarslesTrue()

    val name = "dkif"
    val server: NettyApplicationEngine = embeddedServer(
        factory = Netty,
        port = port,
    ) {
        installContentNegotiation()
        routing {
            get(DkifClient.DKIF_KONTAKTINFO_PATH) {
                if (getHeader(DkifClient.NAV_PERSONIDENTER_HEADER) == ARBEIDSTAKER_PERSONIDENT) {
                    call.respond(dkifResponse)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
