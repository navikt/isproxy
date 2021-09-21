package no.nav.syfo.application.api.access

import com.auth0.jwt.JWT
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.syfo.util.configuredJacksonMapper

const val JWT_CLAIM_AZP = "azp"

fun getConsumerClientId(token: String): String? {
    val decodedJWT = JWT.decode(token)
    return decodedJWT.claims[JWT_CLAIM_AZP]?.asString()
}

class APIConsumerAccessService(
    azureAppPreAuthorizedApps: String
) {
    private val preAuthorizedClientList: List<PreAuthorizedClient> = configuredJacksonMapper()
        .readValue(azureAppPreAuthorizedApps)

    fun validateConsumerApplicationAZP(
        authorizedApplicationNameList: List<String>,
        token: String,
    ) {
        val clientIdList = preAuthorizedClientList
            .filter {
                authorizedApplicationNameList.contains(
                    it.toNamespaceAndApplicationName().applicationName
                )
            }
            .map { it.clientId }

        val consumerClientIdAzp = getConsumerClientId(token = token)
            ?: throw IllegalArgumentException("Claim AZP was not found in token")
        if (!clientIdList.contains(consumerClientIdAzp)) {
            throw ForbiddenProxyConsumer(consumerClientIdAzp = consumerClientIdAzp)
        }
    }
}
