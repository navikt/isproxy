package no.nav.syfo.util

import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.argument.StructuredArguments

fun callIdArgument(callId: String): StructuredArgument = StructuredArguments.keyValue("callId", callId)
fun errorMessageArgument(message: String?): StructuredArgument = StructuredArguments.keyValue("message", message)
fun statusCodeArgument(statusCode: String): StructuredArgument = StructuredArguments.keyValue("statusCode", statusCode)
