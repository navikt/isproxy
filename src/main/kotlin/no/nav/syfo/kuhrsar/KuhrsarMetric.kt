package no.nav.syfo.btsys

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_KUHRSAR_BASE = "${METRICS_NS}_call_kuhrsar"
const val CALL_KUHRSAR_SUCCESS = "${CALL_KUHRSAR_BASE}_success_count"
const val CALL_KUHRSAR_FAIL = "${CALL_KUHRSAR_BASE}_fail_count"

val COUNT_CALL_KUHRSAR_SUCCESS: Counter = Counter
    .builder(CALL_KUHRSAR_SUCCESS)
    .description("Counts the number of successful calls to kuhrsar")
    .register(METRICS_REGISTRY)

val COUNT_CALL_KUHRSAR_FAIL: Counter = Counter
    .builder(CALL_KUHRSAR_FAIL)
    .description("Counts the number of failed calls to kuhrsar")
    .register(METRICS_REGISTRY)
