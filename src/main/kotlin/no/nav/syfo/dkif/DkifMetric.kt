package no.nav.syfo.dkif

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_DKIF_KONTAKTINFORMASJON_BASE = "${METRICS_NS}_call_dkif_kontaktinformasjon"
const val CALL_DKIF_KONTAKTINFORMASJON_SUCCESS = "${CALL_DKIF_KONTAKTINFORMASJON_BASE}_success_count"
const val CALL_DKIF_KONTAKTINFORMASJON_FAIL = "${CALL_DKIF_KONTAKTINFORMASJON_BASE}_fail_count"

val COUNT_CALL_DKIF_KONTAKTINFORMASJON_SUCCESS: Counter = Counter
    .builder(CALL_DKIF_KONTAKTINFORMASJON_SUCCESS)
    .description("Counts the number of successful calls to Dkif - Kontaktinformasjon")
    .register(METRICS_REGISTRY)

val COUNT_CALL_DKIF_KONTAKTINFORMASJON_FAIL: Counter = Counter
    .builder(CALL_DKIF_KONTAKTINFORMASJON_FAIL)
    .description("Counts the number of failed calls to Dkif - Kontaktinformasjon")
    .register(METRICS_REGISTRY)
