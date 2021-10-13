package no.nav.syfo.testhelper

import no.nav.syfo.domain.AktorId
import no.nav.syfo.domain.Virksomhetsnummer

object UserConstants {
    val ARBEIDSTAKER_PERSONIDENT = "12345678912"
    val ARBEIDSTAKER_AKTORID = AktorId("1234567891201")
    val ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID = AktorId(ARBEIDSTAKER_AKTORID.value.replace("1", "2"))
    val ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR = AktorId(ARBEIDSTAKER_AKTORID.value.replace("1", "3"))

    val VIRKSOMHETSNUMMER_DEFAULT = Virksomhetsnummer("912345678")

    const val ENHET_NR = "0123"

    const val FASTLEGEOPPSLAG_PERSON_ID = "10101012345"
    const val FASTLEGE_FORNAVN = "Willy"
    const val FASTLEGE_ETTERNAVN = "Lege etternavn"
    const val FASTLEGE_FNR = "20202012345"
    const val FASTLEGEKONTOR_NAVN = "Fastlegekontoret"
    const val FASTLEGEKONTOR_ORGNR = 123456789
    const val FASTLEGEKONTOR_TLF = "12345678"
    const val FASTLEGEKONTOR_EPOST = "test@nav.no"
    const val FASTLEGEKONTOR_POSTSTED = "Oslo"
    const val FASTLEGEKONTOR_POSTNR = 651
    const val FASTLEGEKONTOR_POSTNR_STRING = "0651"
    const val FASTLEGEKONTOR_ADR = "Storgata 2"
    const val FASTLEGEKONTOR_POSTBOKS = "Boks 99"
    const val HPR_NR = 12345
    const val HER_ID = 1234
    const val PARENT_HER_ID = 9876

    const val VEILEDER_IDENT = "Z999999"
    const val VEILEDER_IDENT_2 = "Z111111"
}
