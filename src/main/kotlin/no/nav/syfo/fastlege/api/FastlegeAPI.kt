package no.nav.syfo.fastlege.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.fastlege.ws.fastlege.FastlegeInformasjonClient
import no.nav.syfo.util.*

const val fastlegeBasePath = "/api/v1/fastlege"

fun Route.registerFastlegeApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    fastlegeClient: FastlegeInformasjonClient,
) {
    route(fastlegeBasePath) {
        get {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Fastlege",
            ) {
                val fnr = getHeader(NAV_PERSONIDENT_HEADER)
                    ?: throw IllegalArgumentException("No fnr given")

                val fastleger = fastlegeClient.hentBrukersFastleger(fnr)

                call.respond(fastleger)
            }
        }
    }
}
