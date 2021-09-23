package no.nav.syfo.ereg

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_EREG_ORGANISASJON_BASE = "${METRICS_NS}_call_ereg_organisasjon"
const val CALL_EREG_ORGANISASJON_SUCCESS = "${CALL_EREG_ORGANISASJON_BASE}_success_count"
const val CALL_EREG_ORGANISASJON_FAIL = "${CALL_EREG_ORGANISASJON_BASE}_fail_count"

val COUNT_CALL_EREG_ORGANISASJON_SUCCESS: Counter = Counter
    .builder(CALL_EREG_ORGANISASJON_SUCCESS)
    .description("Counts the number of successful calls to Ereg - Organisasjon")
    .register(METRICS_REGISTRY)

val COUNT_CALL_EREG_ORGANISASJON_FAIL: Counter = Counter
    .builder(CALL_EREG_ORGANISASJON_FAIL)
    .description("Counts the number of failed calls to Ereg - Organisasjon")
    .register(METRICS_REGISTRY)
