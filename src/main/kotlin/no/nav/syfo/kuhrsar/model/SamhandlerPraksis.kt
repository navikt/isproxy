package no.nav.syfo.kuhrsar.model

import java.util.Date

data class SamhandlerPraksis(
    val org_id: String?,
    val refusjon_type_kode: String?,
    val laerer: String?,
    val lege_i_spesialisering: String?,
    val tidspunkt_resync_periode: Date?,
    val tidspunkt_registrert: Date?,
    val samh_praksis_status_kode: String,
    val telefonnr: String?,
    val arbeids_kommune_nr: String?,
    val arbeids_postnr: String?,
    val arbeids_adresse_linje_1: String?,
    val arbeids_adresse_linje_2: String?,
    val arbeids_adresse_linje_3: String?,
    val arbeids_adresse_linje_4: String?,
    val arbeids_adresse_linje_5: String?,
    val her_id: String?,
    val post_adresse_linje_1: String?,
    val post_adresse_linje_2: String?,
    val post_adresse_linje_3: String?,
    val post_adresse_linje_4: String?,
    val post_adresse_linje_5: String?,
    val post_kommune_nr: String?,
    val post_postnr: String?,
    val tss_ident: String,
    val navn: String?,
    val ident: String?,
    val samh_praksis_type_kode: String?,
    val samh_id: String,
    val samh_praksis_id: String,
    val samh_praksis_periode: List<SamhandlerPeriode>
)

fun SamhandlerPraksis.isAktiv() = samh_praksis_status_kode == "aktiv"

fun SamhandlerPraksis.isInaktiv() = samh_praksis_status_kode == "inaktiv"

fun SamhandlerPraksis.isLegevakt() =
    samh_praksis_type_kode == SamhandlerPraksisType.LEGEVAKT.kodeVerdi ||
        samh_praksis_type_kode == SamhandlerPraksisType.LEGEVAKT_KOMMUNAL.kodeVerdi

fun SamhandlerPraksis.isNotLegevakt() = !this.isLegevakt()

fun SamhandlerPraksis.isFastlege() = samh_praksis_type_kode == SamhandlerPraksisType.FASTLEGE.kodeVerdi

fun SamhandlerPraksis.isFastlonnet() = samh_praksis_type_kode == SamhandlerPraksisType.FASTLONNET.kodeVerdi

enum class SamhandlerPraksisType(val kodeVerdi: String) {
    FASTLEGE("FALE"),
    FASTLONNET("FALO"),
    LEGEVAKT("LEVA"),
    LEGEVAKT_KOMMUNAL("LEKO"),
}
