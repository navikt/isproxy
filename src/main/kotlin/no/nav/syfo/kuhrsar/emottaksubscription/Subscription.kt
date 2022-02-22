package no.nav.syfo.kuhrsar.emottaksubscription

import no.nav.emottak.subscription.StartSubscriptionRequest
import no.nav.emottak.subscription.SubscriptionPort
import org.slf4j.LoggerFactory
import java.lang.Exception

private val logger = LoggerFactory.getLogger(SubscriptionPort::class.java)

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
