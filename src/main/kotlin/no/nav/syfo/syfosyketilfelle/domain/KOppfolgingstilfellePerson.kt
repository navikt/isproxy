package no.nav.syfo.syfosyketilfelle.domain

import java.time.LocalDateTime

data class KOppfolgingstilfellePerson(
    val aktorId: String,
    val tidslinje: List<KSyketilfelledag>,
    val sisteDagIArbeidsgiverperiode: KSyketilfelledag,
    val antallBrukteDager: Int,
    val oppbruktArbeidsgvierperiode: Boolean,
    val utsendelsestidspunkt: LocalDateTime,
)
