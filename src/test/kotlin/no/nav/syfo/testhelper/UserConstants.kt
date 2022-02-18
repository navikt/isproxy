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
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_PRAKSIS_NAME = "10101012346"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR = "11101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR = "12101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID = "13101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR = "14101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN = "15101012345"
    const val FASTLEGE_FORNAVN = "Willy"
    const val FASTLEGE_ETTERNAVN = "Lege etternavn"
    const val FASTLEGE_FNR = "20202012345"
    const val FASTLEGE_RELASJON_KODEVERDI = "LPFL"
    const val FASTLEGE_RELASJON_KODETEKST = "Fastlege"

    const val FASTLEGE_VIKAR_FORNAVN = "Vikar"
    const val FASTLEGE_VIKAR_ETTERNAVN = "Legevikar etternavn"
    const val FASTLEGE_VIKAR_FNR = "10202012345"
    const val FASTLEGE_VIKAR_RELASJON_KODEVERDI = "LPVI"
    const val FASTLEGE_VIKAR_RELASJON_KODETEKST = "Vikar"
    const val FASTLEGE_VIKAR_HPR_NR = 23456

    const val FASTLEGEKONTOR_NAVN = "Fastlegekontoret"
    const val FASTLEGEKONTOR_ORGNR = 123456789
    const val FASTLEGEKONTOR_TLF = "12345678"
    const val FASTLEGEKONTOR_EPOST = "test@nav.no"
    const val FASTLEGEKONTOR_POSTSTED = "Oslo"
    const val FASTLEGEKONTOR_POSTNR = 651
    const val FASTLEGEKONTOR_POSTNR_STRING = "0651"
    const val FASTLEGEKONTOR_ADR = "Storgata 2"
    const val FASTLEGEKONTOR_POSTBOKS = "Boks 99"

    const val FASTLEGE_HPR_NR = 12345
    const val HER_ID = 1234
    const val PARENT_HER_ID = 9876

    const val VEILEDER_IDENT = "Z999999"
    const val VEILEDER_IDENT_2 = "Z111111"

    val AZUREAD_TOKEN = "tokenReturnedByAzureAd"
}
