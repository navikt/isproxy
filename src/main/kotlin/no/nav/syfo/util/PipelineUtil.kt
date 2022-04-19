package no.nav.syfo.util

import io.ktor.server.application.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.application.api.access.ForbiddenProxyConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("no.nav.syfo")

fun PipelineContext<out Unit, ApplicationCall>.getCallId(): String {
    return this.call.request.headers[NAV_CALL_ID_HEADER].toString()
}

fun PipelineContext<out Unit, ApplicationCall>.getBearerHeader(): String? {
    return this.call.request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
}

fun PipelineContext<out Unit, ApplicationCall>.getHeader(header: String): String? {
    return this.call.request.headers[header]
}

suspend fun PipelineContext<out Unit, ApplicationCall>.proxyRequestHandler(
    authorizedApplicationNameList: List<String>,
    apiConsumerAccessService: APIConsumerAccessService,
    proxyServiceName: String,
    requestBlock: suspend () -> Unit,
) {
    try {
        val token = getBearerHeader()
            ?: throw IllegalArgumentException("No Authorization header supplied")

        apiConsumerAccessService.validateConsumerApplicationAZP(
            authorizedApplicationNameList = authorizedApplicationNameList,
            token = token,
        )

        requestBlock()
    } catch (ex: Exception) {
        handleProxyError(
            ex = ex,
            proxyServiceName = proxyServiceName,
        )
    }
}

suspend fun PipelineContext<out Unit, ApplicationCall>.handleProxyError(
    ex: Exception,
    proxyServiceName: String,
) {
    val callId = getCallId()
    val responseStatus: HttpStatusCode = when (ex) {
        is ResponseException -> {
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
    val message = "Failed to get response from $proxyServiceName, callId=$callId, message=${ex.message}"
    log.error("Failed to proxy $proxyServiceName: status=${responseStatus.value} message=$message callId=$callId")
    call.respond(responseStatus, message)
}
