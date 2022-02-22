package no.nav.syfo.kuhrsar.emottaksubscription

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_SUBSCRIPTION_BASE = "${METRICS_NS}_call_subscription"
const val CALL_SUBSCRIPTION_SUCCESS = "${CALL_SUBSCRIPTION_BASE}_success_count"
const val CALL_SUBSCRIPTION_FAIL = "${CALL_SUBSCRIPTION_BASE}_fail_count"

val COUNT_CALL_SUBSCRIPTION_SUCCESS: Counter = Counter
    .builder(CALL_SUBSCRIPTION_SUCCESS)
    .description("Counts the number of successful calls to emottak subscription")
    .register(METRICS_REGISTRY)

val COUNT_CALL_SUBSCRIPTION_FAIL: Counter = Counter
    .builder(CALL_SUBSCRIPTION_FAIL)
    .description("Counts the number of failed calls to emottak subscription")
    .register(METRICS_REGISTRY)
