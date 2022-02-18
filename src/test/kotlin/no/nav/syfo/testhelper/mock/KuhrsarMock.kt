package no.nav.syfo.testhelper.mock

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.kuhrsar.client.*
import no.nav.syfo.kuhrsar.model.*
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_PRAKSIS_NAME
import no.nav.syfo.testhelper.UserConstants.HER_ID
import no.nav.syfo.testhelper.getRandomPort
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class KuhrsarMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"
    val name = "kuhrsar"
    val server = mockKuhrSarServer(
        port
    )

    private fun mockKuhrSarServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            installContentNegotiation()
            routing {
                get(KuhrSarClient.KUHRSAR_PATH) {
                    val ident = call.parameters.get("ident")
                    call.respond(
                        if (ident == FASTLEGEOPPSLAG_PERSON_ID) {
                            listOf(
                                createSamhandler(
                                    personIdent = FASTLEGEOPPSLAG_PERSON_ID,
                                    praksisNavn = "Kule helsetjenester",
                                    herId = "$HER_ID",
                                    tssId = "123",
                                    typeKode = null,
                                ),
                            )
                        } else if (ident == FASTLEGEOPPSLAG_PERSON_ID_MISSING_PRAKSIS_NAME) {
                            listOf(
                                createSamhandler(
                                    personIdent = FASTLEGEOPPSLAG_PERSON_ID_MISSING_PRAKSIS_NAME,
                                    praksisNavn = null,
                                    herId = "${HER_ID}xx",
                                    tssId = "1234",
                                    typeKode = SamhandlerPraksisType.FASTLONNET,
                                ),
                            )
                        } else emptyList()
                    )
                }
            }
        }
    }

    private fun createSamhandler(
        personIdent: String,
        praksisNavn: String?,
        herId: String,
        tssId: String,
        typeKode: SamhandlerPraksisType?,
    ) = Samhandler(
        samh_id = personIdent,
        navn = "Kul samhandler",
        samh_type_kode = "",
        behandling_utfall_kode = "",
        unntatt_veiledning = "",
        godkjent_manuell_krav = "",
        ikke_godkjent_for_refusjon = "",
        godkjent_egenandel_refusjon = "",
        godkjent_for_fil = "",
        endringslogg_tidspunkt_siste = null,
        samh_praksis = listOf(
            SamhandlerPraksis(
                org_id = null,
                refusjon_type_kode = null,
                laerer = null,
                lege_i_spesialisering = null,
                tidspunkt_resync_periode = null,
                tidspunkt_registrert = null,
                samh_praksis_status_kode = "aktiv",
                telefonnr = null,
                arbeids_kommune_nr = null,
                arbeids_postnr = null,
                arbeids_adresse_linje_1 = null,
                arbeids_adresse_linje_2 = null,
                arbeids_adresse_linje_3 = null,
                arbeids_adresse_linje_4 = null,
                arbeids_adresse_linje_5 = null,
                her_id = herId,
                post_adresse_linje_1 = null,
                post_adresse_linje_2 = null,
                post_adresse_linje_3 = null,
                post_adresse_linje_4 = null,
                post_adresse_linje_5 = null,
                post_kommune_nr = null,
                post_postnr = null,
                tss_ident = tssId,
                navn = praksisNavn,
                ident = null,
                samh_praksis_type_kode = typeKode?.kodeVerdi,
                samh_id = "samh_id",
                samh_praksis_id = "samh_praksis_id",
                samh_praksis_periode = listOf(
                    SamhandlerPeriode(
                        slettet = "",
                        gyldig_fra = Date.from(Instant.now().minus(5, ChronoUnit.DAYS)),
                        gyldig_til = null,
                        samh_praksis_id = "samh_praksis_id",
                        samh_praksis_periode_id = "1",
                    )
                ),
            ),
        )
    )
}
