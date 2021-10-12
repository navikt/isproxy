package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.syfosyketilfelle.client.SyfosyketilfelleClient.Companion.SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH
import no.nav.syfo.syfosyketilfelle.client.SyfosyketilfelleClient.Companion.SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_UTEN_ARBEIDSGIVER_PATH
import no.nav.syfo.syfosyketilfelle.domain.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR
import no.nav.syfo.testhelper.UserConstants.VIRKSOMHETSNUMMER_DEFAULT
import no.nav.syfo.testhelper.getRandomPort
import java.time.LocalDate
import java.time.LocalDateTime

fun generateOppfolgingstilfellePerson() =
    KOppfolgingstilfellePerson(
        aktorId = ARBEIDSTAKER_AKTORID.value,
        tidslinje = listOf(
            KSyketilfelledag(
                LocalDate.now().minusDays(10),
                null,
            ),
            KSyketilfelledag(
                LocalDate.now().plusDays(10),
                null,
            ),
        ),
        sisteDagIArbeidsgiverperiode = KSyketilfelledag(
            LocalDate.now().minusDays(1),
            null,
        ),
        antallBrukteDager = 0,
        oppbruktArbeidsgvierperiode = false,
        utsendelsestidspunkt = LocalDateTime.now(),
    )

fun generateOppfolgingstilfelle() =
    KOppfolgingstilfelle(
        aktorId = ARBEIDSTAKER_AKTORID.value,
        orgnummer = VIRKSOMHETSNUMMER_DEFAULT.value,
        tidslinje = listOf(
            KSyketilfelledag(
                LocalDate.now().minusDays(10),
                null,
            ),
            KSyketilfelledag(
                LocalDate.now().plusDays(10),
                null,
            ),
        ),
        sisteDagIArbeidsgiverperiode = KSyketilfelledag(
            LocalDate.now().minusDays(1),
            null,
        ),
        antallBrukteDager = 0,
        oppbruktArbeidsgvierperiode = false,
        utsendelsestidspunkt = LocalDateTime.now(),
    )

class SyfosyketilfelleMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"

    val oppfolgingstilfellePersonResponse = generateOppfolgingstilfellePerson()
    val oppfolgingstilfelleResponse = generateOppfolgingstilfelle()

    val name = "syfosyketilfelle"
    val server = mockServer()

    private fun mockServer(): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_AKTORID.value}") {
                    call.respond(oppfolgingstilfellePersonResponse)
                }
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID.value}") {
                    call.respond(HttpStatusCode.NoContent)
                }
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}") {
                    call.respond(HttpStatusCode.BadRequest)
                }

                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_AKTORID.value}/$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_UTEN_ARBEIDSGIVER_PATH") {
                    call.respond(oppfolgingstilfellePersonResponse)
                }
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID.value}$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_UTEN_ARBEIDSGIVER_PATH") {
                    call.respond(HttpStatusCode.NoContent)
                }
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_UTEN_ARBEIDSGIVER_PATH") {
                    call.respond(HttpStatusCode.BadRequest)
                }

                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_AKTORID.value}/${VIRKSOMHETSNUMMER_DEFAULT.value}") {
                    call.respond(oppfolgingstilfelleResponse)
                }
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID.value}/${VIRKSOMHETSNUMMER_DEFAULT.value}") {
                    call.respond(HttpStatusCode.NoContent)
                }
                get("$SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_PATH/${ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}/${VIRKSOMHETSNUMMER_DEFAULT.value}") {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
