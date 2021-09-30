package no.nav.syfo.axsys

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_AXSYS_BASE = "${METRICS_NS}_call_axsys"
const val CALL_AXSYS_ENHET_BRUKERE_BASE = "${CALL_AXSYS_BASE}_enhet_brukere"
const val CALL_AXSYS_ENHET_BRUKERE_SUCCESS = "${CALL_AXSYS_ENHET_BRUKERE_BASE}_success_count"
const val CALL_AXSYS_ENHET_BRUKERE_FAIL = "${CALL_AXSYS_ENHET_BRUKERE_BASE}_fail_count"

val COUNT_CALL_AXSYS_ENHET_BRUKERE_SUCCESS: Counter = Counter
    .builder(CALL_AXSYS_ENHET_BRUKERE_SUCCESS)
    .description("Counts the number of successful calls to Axsys - Enhet-brukere")
    .register(METRICS_REGISTRY)

val COUNT_CALL_AXSYS_ENHET_BRUKERE_FAIL: Counter = Counter
    .builder(CALL_AXSYS_ENHET_BRUKERE_FAIL)
    .description("Counts the number of failed calls to Axsys - Enhet-brukere")
    .register(METRICS_REGISTRY)
