package no.nav.syfo.fastlege.ws.fastlege

import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY
import io.micrometer.core.instrument.Counter

const val CALL_FASTLEGE_BASE = "${METRICS_NS}_call_fastlege"
const val CALL_FASTLEGE_SUCCESS = "${CALL_FASTLEGE_BASE}_success_count"
const val CALL_FASTLEGE_NOT_FOUND = "${CALL_FASTLEGE_BASE}_not_found_count"
const val CALL_FASTLEGE_FAIL = "${CALL_FASTLEGE_BASE}_fail_count"

val COUNT_FASTLEGE_SUCCESS: Counter = Counter
    .builder(CALL_FASTLEGE_SUCCESS)
    .description("Counts the number of successful calls to Fastlegeregisteret")
    .register(METRICS_REGISTRY)
val COUNT_FASTLEGE_NOT_FOUND: Counter = Counter
    .builder(CALL_FASTLEGE_NOT_FOUND)
    .description("Counts the number of calls to Fastlegeregisteret where result is not found")
    .register(METRICS_REGISTRY)
val COUNT_FASTLEGE_FAIL: Counter = Counter
    .builder(CALL_FASTLEGE_FAIL)
    .description("Counts the number of failed calls to Fastlegeregisteret")
    .register(METRICS_REGISTRY)
