package no.nav.syfo.dokdist

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_DOKDIST_DISTRIBUER_JOURNALPOST_BASE = "${METRICS_NS}_call_dokdist_distribuer_journalpost"
const val CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS = "${CALL_DOKDIST_DISTRIBUER_JOURNALPOST_BASE}_success_count"
const val CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL = "${CALL_DOKDIST_DISTRIBUER_JOURNALPOST_BASE}_fail_count"

val COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS: Counter = Counter
    .builder(CALL_DOKDIST_DISTRIBUER_JOURNALPOST_SUCCESS)
    .description("Counts the number of successful calls to Dokdist - Distribuer Journalpost")
    .register(METRICS_REGISTRY)
val COUNT_CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL: Counter = Counter
    .builder(CALL_DOKDIST_DISTRIBUER_JOURNALPOST_FAIL)
    .description("Counts the number of failed calls to Dokdist - Distribuer Journalpost")
    .register(METRICS_REGISTRY)
