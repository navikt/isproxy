package no.nav.syfo.dokdist.domain

data class DokdistRequest(
    val journalpostId: String,
    val bestillendeFagsystem: String,
    val dokumentProdApp: String,
)
