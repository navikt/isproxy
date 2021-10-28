package no.nav.syfo.norg2.domain

data class ArbeidsfordelingCriteria(
    val diskresjonskode: String? = null,
    val oppgavetype: String? = null,
    val behandlingstype: String,
    val behandlingstema: String? = null,
    val tema: String,
    val temagruppe: String? = null,
    val geografiskOmraade: String? = null,
    val enhetNummer: String? = null,
    val skjermet: Boolean,
)
