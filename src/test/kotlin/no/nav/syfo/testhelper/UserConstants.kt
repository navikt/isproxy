package no.nav.syfo.testhelper

import no.nav.syfo.domain.AktorId

object UserConstants {
    val ARBEIDSTAKER_AKTORID = AktorId("1234567891201")
    val ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID = AktorId(ARBEIDSTAKER_AKTORID.value.replace("1", "2"))
    val ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR = AktorId(ARBEIDSTAKER_AKTORID.value.replace("1", "3"))

    const val ENHET_NR = "0123"

    const val VEILEDER_IDENT = "Z999999"
    const val VEILEDER_IDENT_2 = "Z111111"
}
