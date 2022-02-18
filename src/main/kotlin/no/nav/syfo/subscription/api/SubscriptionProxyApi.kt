package no.nav.syfo.ereg.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.emottak.subscription.StartSubscriptionRequest
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.subscription.api.COUNT_CALL_SUBSCRIPTION_FAIL
import no.nav.syfo.subscription.api.COUNT_CALL_SUBSCRIPTION_SUCCESS
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import java.lang.Exception

const val subscriptionProxyBasePath = "/api/v1/subscription"

val logger = LoggerFactory.getLogger("no.nav.syfo.subscription.api")

fun Route.registerSubscriptionProxyApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    subscriptionEmottak: SubscriptionPort,
) {
    route(subscriptionProxyBasePath) {
        post() {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "subscription",
            ) {
                val subscriptionRequest = call.receive<SubscriptionRequest>()
                startSubscription(subscriptionEmottak, subscriptionRequest)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun startSubscription(
    subscriptionEmottak: SubscriptionPort,
    request: SubscriptionRequest,
) {
    try {
        val response = subscriptionEmottak.startSubscription(
            StartSubscriptionRequest().apply {
                key = request.tssIdent
                data = request.data
                partnerid = request.partnerId
            }
        )
        logger.info("Started emottak subscription for ${request.tssIdent}, got response: ${response.status} (key: ${response.key}, description: ${response.description})")
        COUNT_CALL_SUBSCRIPTION_SUCCESS.increment()
    } catch (e: Exception) {
        COUNT_CALL_SUBSCRIPTION_FAIL.increment()
        throw e
    }
}

data class SubscriptionRequest(
    val tssIdent: String,
    val partnerId: Int,
    val data: ByteArray
)
