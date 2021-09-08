package no.nav.syfo.util

import io.ktor.application.*
import io.ktor.util.pipeline.*
import net.logstash.logback.argument.StructuredArguments
import java.util.Base64

const val NAV_CALL_ID_HEADER = "Nav-Call-Id"
fun PipelineContext<out Unit, ApplicationCall>.getCallId(): String {
    return this.call.request.headers[NAV_CALL_ID_HEADER].toString()
}

fun bearerHeader(token: String) = "Bearer $token"

fun callIdArgument(callId: String) = StructuredArguments.keyValue("callId", callId)!!

fun basicHeader(
    credentialUsername: String,
    credentialPassword: String
) = "Basic " + Base64.getEncoder().encodeToString(java.lang.String.format("%s:%s", credentialUsername, credentialPassword).toByteArray())
