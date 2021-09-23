package no.nav.syfo.metric

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Counter.builder
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

const val METRICS_NS = "isproxy"

val METRICS_REGISTRY = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

const val CALL_STS_BASE = "${METRICS_NS}_call_sts"
const val CALL_STS_SUCCESS = "${CALL_STS_BASE}_success"
const val CALL_STS_FAIL = "${CALL_STS_BASE}_fail"

val COUNT_CALL_STS_SUCCESS: Counter = builder(
    CALL_STS_SUCCESS
).description("Counts the number of successful calls to STS").register(METRICS_REGISTRY)
val COUNT_CALL_STS_FAIL: Counter = builder(
    CALL_STS_FAIL
).description("Counts the number of failed calls to STS").register(METRICS_REGISTRY)
