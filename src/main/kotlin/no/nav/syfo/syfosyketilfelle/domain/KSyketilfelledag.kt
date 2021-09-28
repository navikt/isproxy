package no.nav.syfo.syfosyketilfelle.domain

import java.time.LocalDate

data class KSyketilfelledag(
    val dag: LocalDate,
    val prioritertSyketilfellebit: KSyketilfellebit?,
)
