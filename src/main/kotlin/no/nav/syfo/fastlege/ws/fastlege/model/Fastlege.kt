package no.nav.syfo.fastlege.ws.fastlege.model

data class Fastlege(
    val fornavn: String,
    val mellomnavn: String,
    val etternavn: String,
    val fnr: String,
    val herId: Int?,
    val helsepersonellregisterId: String?,
    val fastlegekontor: Fastlegekontor,
    val pasientforhold: Pasientforhold,
)
