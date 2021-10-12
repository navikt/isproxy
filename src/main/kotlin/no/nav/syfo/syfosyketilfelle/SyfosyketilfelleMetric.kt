package no.nav.syfo.syfosyketilfelle

import io.micrometer.core.instrument.Counter
import no.nav.syfo.metric.METRICS_NS
import no.nav.syfo.metric.METRICS_REGISTRY

const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE = "${METRICS_NS}_call_syfosyketilfelle_oppfolgingstilfelle_person"
const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_SUCCESS = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE}_success_count"
const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_FAIL = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE}_fail_count"

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_SUCCESS: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_SUCCESS)
    .description("Counts the number of successful calls to Syfosyketilfelle - Oppfolgingstilfelle Person")
    .register(METRICS_REGISTRY)

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_FAIL: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_FAIL)
    .description("Counts the number of failed calls to Syfosyketilfelle - Oppfolgingstilfelle Person")
    .register(METRICS_REGISTRY)

const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_VIRKSOMHET_SUCCESS = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE}_virksomhet_success_count"
const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_VIRKSOMHET_FAIL = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE}_virksomhet_fail_count"

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_VIRKSOMHET_SUCCESS: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_VIRKSOMHET_SUCCESS)
    .description("Counts the number of successful calls to Syfosyketilfelle - Oppfolgingstilfelle Person and Virksomhet")
    .register(METRICS_REGISTRY)

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_VIRKSOMHET_FAIL: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_VIRKSOMHET_FAIL)
    .description("Counts the number of failed calls to Syfosyketilfelle - Oppfolgingstilfelle Person and Virksomhet")
    .register(METRICS_REGISTRY)

const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_PERSON_UTEN_ARBEIDSGIVER_SUCCESS = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE}_uten_arbeidsgiver_success_count"
const val CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_PERSON_UTEN_ARBEIDSGIVER_FAIL = "${CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_BASE}_uten_arbeidsgiver_fail_count"

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_UTEN_ARBEIDSGIVER_SUCCESS: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_PERSON_UTEN_ARBEIDSGIVER_SUCCESS)
    .description("Counts the number of successful calls to Syfosyketilfelle - Oppfolgingstilfelle Person and no Virksomhet")
    .register(METRICS_REGISTRY)

val COUNT_CALL_SYFOSYKETILFELLE_OPPFOLGINGSTILFELLE_PERSON_UTEN_ARBEIDSGIVER_FAIL: Counter = Counter
    .builder(CALL_SYFOSYKETILFELLE_OPPFOLGINGSTIFELLE_PERSON_UTEN_ARBEIDSGIVER_FAIL)
    .description("Counts the number of failed calls to Syfosyketilfelle - Oppfolgingstilfelle Person and no Virksomhet")
    .register(METRICS_REGISTRY)
