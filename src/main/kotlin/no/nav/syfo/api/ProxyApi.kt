package no.nav.syfo.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.client.dokdist.DokdistClient
import no.nav.syfo.client.dokdist.DokdistRequest
import no.nav.syfo.util.callIdArgument
import no.nav.syfo.util.getCallId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("no.nav.syfo")

const val proxyPath = "/api/v1"
const val distribuerJournalpostPath = "/distribuerJournalpost"

fun Route.registerProxyApi(
    dokdistClient: DokdistClient
) {
    route(proxyPath) {
        post(distribuerJournalpostPath) {
            val callId = getCallId()
            try {
                val dokdistRequest = call.receive<DokdistRequest>()
                val dokdistResponse =
                    dokdistClient.distribuerJournalpost(dokdistRequest, callId)
                        ?: throw RuntimeException("Failed to distribute journalpost: missing response")
                call.respond(dokdistResponse)
            } catch (e: Exception) {
                val message = "Could not distribute journalpost"
                log.error("$message: {}, {}", e.message, callIdArgument(callId), e)
                call.respond(HttpStatusCode.BadRequest, e.message ?: message)
            }
        }
    }
}
