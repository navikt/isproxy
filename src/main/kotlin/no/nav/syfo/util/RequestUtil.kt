package no.nav.syfo.util

import io.ktor.application.*
import io.ktor.client.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.syfo.application.api.access.ForbiddenProxyConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

private val log: Logger = LoggerFactory.getLogger("no.nav.syfo")

const val NAV_CALL_ID_HEADER = "Nav-Call-Id"
const val NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token"

fun PipelineContext<out Unit, ApplicationCall>.getCallId(): String {
    return this.call.request.headers[NAV_CALL_ID_HEADER].toString()
}

fun PipelineContext<out Unit, ApplicationCall>.getBearerHeader(): String? {
    return this.call.request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
}

fun bearerHeader(token: String) = "Bearer $token"

fun callIdArgument(callId: String) = StructuredArguments.keyValue("callId", callId)!!

fun basicHeader(
    credentialUsername: String,
    credentialPassword: String
) = "Basic " + Base64.getEncoder().encodeToString(java.lang.String.format("%s:%s", credentialUsername, credentialPassword).toByteArray())

suspend fun PipelineContext<out Unit, ApplicationCall>.handleProxyError(
    ex: Exception,
    proxyServiceName: String,
) {
    val responseStatus: HttpStatusCode = when (ex) {
        is ClientRequestException -> {
            ex.response.status
        }
        is ServerResponseException -> {
            ex.response.status
        }
        is IllegalArgumentException -> {
            HttpStatusCode.BadRequest
        }
        is ForbiddenProxyConsumer -> {
            HttpStatusCode.Forbidden
        }
        else -> {
            HttpStatusCode.InternalServerError
        }
    }
    val callId = getCallId()
    val message = "Failed to get response from $proxyServiceName<, callId=$callId, message=${ex.message}"
    log.error("Failed to proxy $proxyServiceName: status=${responseStatus.value} message=$message")
    call.respond(responseStatus, message)
}
