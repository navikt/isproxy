package no.nav.syfo.util

import java.util.*

const val NAV_CALL_ID_HEADER = "Nav-Call-Id"
const val NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token"

fun bearerHeader(token: String) = "Bearer $token"

fun basicHeader(
    credentialUsername: String,
    credentialPassword: String
) = "Basic " + Base64.getEncoder()
    .encodeToString(java.lang.String.format("%s:%s", credentialUsername, credentialPassword).toByteArray())
