package no.nav.syfo.application.api.access

class ForbiddenProxyConsumer(
    consumerClientIdAzp: String,
    message: String = "Consumer with clientId(azp)=$consumerClientIdAzp is denied access to system API",
) : RuntimeException(message)
