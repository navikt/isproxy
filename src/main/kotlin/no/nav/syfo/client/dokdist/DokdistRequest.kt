package no.nav.syfo.client.dokdist

data class DokdistRequest(
    val journalpostId: String,
    val bestillendeFagsystem: String,
    val dokumentProdApp: String,
)
