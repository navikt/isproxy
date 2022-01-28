package no.nav.syfo.btsys

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_BTSYS_SUSPENSJON_BASE = "${METRICS_NS}_call_btsys_suspensjon"
const val CALL_BTSYS_SUSPENSJON_SUCCESS = "${CALL_BTSYS_SUSPENSJON_BASE}_success_count"
const val CALL_BTSYS_SUSPENSJON_FAIL = "${CALL_BTSYS_SUSPENSJON_BASE}_fail_count"

val COUNT_CALL_BTSYS_SUSPENSJON_SUCCESS: Counter = Counter
    .builder(CALL_BTSYS_SUSPENSJON_SUCCESS)
    .description("Counts the number of successful calls to btsys - suspensjon")
    .register(METRICS_REGISTRY)

val COUNT_CALL_BTSYS_SUSPENSJON_FAIL: Counter = Counter
    .builder(CALL_BTSYS_SUSPENSJON_FAIL)
    .description("Counts the number of failed calls to btsys - suspensjon")
    .register(METRICS_REGISTRY)
