package no.nav.syfo.syfosyketilfelle

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_BASE = "${METRICS_NS}_call_syfosyketilfelle_oppfolgingstilfelle_person"
const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_SUCCESS = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_BASE}_success_count"
const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_FAIL = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_BASE}_fail_count"

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_SUCCESS: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_SUCCESS)
    .description("Counts the number of successful calls to Syfosyketilfelle - Oppfolgingstilfelle Person")
    .register(METRICS_REGISTRY)

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_FAIL: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_FAIL)
    .description("Counts the number of failed calls to Syfosyketilfelle - Oppfolgingstilfelle Person")
    .register(METRICS_REGISTRY)
