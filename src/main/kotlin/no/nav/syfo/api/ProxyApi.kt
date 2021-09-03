package no.nav.syfo.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("no.nav.syfo")

const val proxyPath = "/api/v1/send"
const val testPath = "/test"

fun Route.registerProxyApi() {
    route(proxyPath) {
        get(testPath) {
            try {
                call.respond(HttpStatusCode.OK, "Vellykket!")
            } catch (e: IllegalArgumentException) {
                val illegalArgumentMessage = "Could not get from proxyApi"
                log.warn("$illegalArgumentMessage: {}", e.message)
                call.respond(HttpStatusCode.BadRequest, e.message ?: illegalArgumentMessage)
            }
        }
    }
}
