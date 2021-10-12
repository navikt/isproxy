package no.nav.syfo.fastlege.ws.adresseregister

import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY
import io.micrometer.core.instrument.Counter

const val CALL_ADRESSEREGISTER_BASE = "${METRICS_NS}_call_adresseregister"
const val CALL_ADRESSEREGISTER_SUCCESS = "${CALL_ADRESSEREGISTER_BASE}_success_count"
const val CALL_ADRESSEREGISTER_NOT_FOUND = "${CALL_ADRESSEREGISTER_BASE}_not_found_count"
const val CALL_ADRESSEREGISTER_FAIL = "${CALL_ADRESSEREGISTER_BASE}_fail_count"

val COUNT_ADRESSEREGISTER_SUCCESS: Counter = Counter
    .builder(CALL_ADRESSEREGISTER_SUCCESS)
    .description("Counts the number of successful calls to adresseregisteret")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_NOT_FOUND: Counter = Counter
    .builder(CALL_ADRESSEREGISTER_NOT_FOUND)
    .description("Counts the number of calls to adresseregisteret where result is not found")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_FAIL: Counter = Counter
    .builder(CALL_ADRESSEREGISTER_FAIL)
    .description("Counts the number of failed calls to adresseregisteret")
    .register(METRICS_REGISTRY)
