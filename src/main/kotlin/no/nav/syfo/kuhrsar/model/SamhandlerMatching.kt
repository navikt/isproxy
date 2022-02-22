package no.nav.syfo.kuhrsar.model

import net.logstash.logback.argument.StructuredArguments
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.kuhrsar.emottaksubscription.SubscriptionRequest
import no.nav.syfo.kuhrsar.emottaksubscription.startSubscription
import org.apache.commons.text.similarity.LevenshteinDistance
import org.slf4j.LoggerFactory
import kotlin.math.max

private val logger = LoggerFactory.getLogger(Samhandler::class.java)

fun List<Samhandler>.findBestSamhandlerPraksis(
    orgName: String,
    legekontorHerId: String?,
    partnerId: Int?,
    data: ByteArray,
    subscriptionPort: SubscriptionPort,
): SamhandlerPraksis? {
    val samhandlerPraksisMatch = this.findBestSamhandlerPraksisMatch(orgName, legekontorHerId)
    val samhandlerPraksis = samhandlerPraksisMatch?.samhandlerPraksis
    if (samhandlerPraksisMatch?.percentageMatch == 999.0) {
        logger.info(
            "SamhandlerPraksis is found but is FALE or FALO, subscription_emottak is not created",
        )
    } else if (samhandlerPraksis != null && samhandlerPraksis.isNotLegevakt() && partnerId != null) {
        startSubscription(
            subscriptionEmottak = subscriptionPort,
            request = SubscriptionRequest(
                tssIdent = samhandlerPraksis.tss_ident,
                partnerId = partnerId,
                data = data,
            )
        )
    }
    return samhandlerPraksis
}

fun List<Samhandler>.findBestSamhandlerPraksisMatch(
    orgName: String,
    legekontorHerId: String?,
): SamhandlerPraksisMatch? {
    val aktiveSamhandlere = this.flatMap { it.samh_praksis }
        .filter { it.isAktiv() }

    val samhandlerPraksisByHerId = aktiveSamhandlere.getSamhandlerPraksisByHerId(legekontorHerId)
    if (samhandlerPraksisByHerId != null) {
        logger.info(
            "Fant samhandler basert på herid. herid: $legekontorHerId, {}",
            StructuredArguments.keyValue("praksisinformasjon", this.formaterPraksis()),
        )
        return SamhandlerPraksisMatch(samhandlerPraksisByHerId, 100.0)
    }

    val aktiveSamhandlereMedNavn = aktiveSamhandlere
        .filter { !it.navn.isNullOrEmpty() }

    if (
        erAlleAktiveSamhandlereUtenNavn(
            aktiveSamhandlereMedNavn = aktiveSamhandlereMedNavn,
            aktiveSamhandlere = aktiveSamhandlere,
        )
    ) {
        val samhandlerFALEOrFALO = aktiveSamhandlere.find {
            it.isFastlege() || it.isFastlonnet()
        }
        if (samhandlerFALEOrFALO != null) {
            return SamhandlerPraksisMatch(samhandlerFALEOrFALO, 999.0)
        }
    } else if (aktiveSamhandlere.isEmpty()) {
        val samhandlerPraksisByOrgName = getInactiveSamhandlerPraksisByOrgName(orgName)
        return samhandlerPraksisMatchTest(
            samhandlerPraksisByOrgName,
            70.0,
            orgName,
        )
    }

    return aktiveSamhandlereMedNavn.getSamhandlerPraksisByOrgName(orgName)
}

fun List<SamhandlerPraksis>.getSamhandlerPraksisByHerId(
    legekontorHerId: String?,
): SamhandlerPraksis? {
    return if (legekontorHerId != null) {
        this.find {
            it.her_id == legekontorHerId
        }
    } else {
        null
    }
}

fun List<SamhandlerPraksis>.getSamhandlerPraksisByOrgName(
    orgName: String
): SamhandlerPraksisMatch? {
    val praksisWithMostSimilarOrgName =
        this.map { samhandlerPraksis ->
            SamhandlerPraksisMatch(samhandlerPraksis, calculatePercentageStringMatch(samhandlerPraksis.navn, orgName) * 100)
        }.maxByOrNull { it.percentageMatch }
    return samhandlerPraksisMatchTest(
        praksisWithMostSimilarOrgName,
        70.0,
        orgName,
    )
}

fun erAlleAktiveSamhandlereUtenNavn(
    aktiveSamhandlereMedNavn: List<SamhandlerPraksis>,
    aktiveSamhandlere: List<SamhandlerPraksis>
) = aktiveSamhandlereMedNavn.isEmpty() && aktiveSamhandlere.isNotEmpty()

fun List<Samhandler>.getInactiveSamhandlerPraksisByOrgName(orgName: String): SamhandlerPraksisMatch? {
    val inaktiveSamhandlereMedNavn = this.flatMap { it.samh_praksis }
        .filter { samhandlerPraksis -> samhandlerPraksis.isInaktiv() }
        .filter { samhandlerPraksis -> !samhandlerPraksis.navn.isNullOrEmpty() }
    return inaktiveSamhandlereMedNavn.map { samhandlerPraksis ->
        SamhandlerPraksisMatch(samhandlerPraksis, calculatePercentageStringMatch(samhandlerPraksis.navn?.lowercase(), orgName.lowercase()) * 100)
    }.maxByOrNull { it.percentageMatch }
}

fun samhandlerPraksisMatchTest(
    samhandlerPraksis: SamhandlerPraksisMatch?,
    percentageMatchLimit: Double,
    orgName: String,
): SamhandlerPraksisMatch? {
    return if (samhandlerPraksis != null && samhandlerPraksis.percentageMatch >= percentageMatchLimit) {
        logger.info(
            "Beste match ble samhandler praksis: " +
                "Orgnumer: ${samhandlerPraksis.samhandlerPraksis.org_id} " +
                "Navn: ${samhandlerPraksis.samhandlerPraksis.navn} " +
                "Tssid: ${samhandlerPraksis.samhandlerPraksis.tss_ident} " +
                "Adresselinje1: ${samhandlerPraksis.samhandlerPraksis.arbeids_adresse_linje_1} " +
                "Samhandler praksis type: ${samhandlerPraksis.samhandlerPraksis.samh_praksis_type_kode} " +
                "Prosent match:${samhandlerPraksis.percentageMatch} %, basert på dialogmeldingen organisjons navn: $orgName "
        )
        samhandlerPraksis
    } else {
        null
    }
}

data class SamhandlerPraksisMatch(
    val samhandlerPraksis: SamhandlerPraksis,
    val percentageMatch: Double,
)

fun calculatePercentageStringMatch(str1: String?, str2: String): Double {
    val maxDistance = max(str1?.length!!, str2.length).toDouble()
    val distance = LevenshteinDistance().apply(str2, str1).toDouble()
    return (maxDistance - distance) / maxDistance
}

fun List<SamhandlerPeriode>.formaterPerioder() = joinToString(",", "periode(", ") ") { periode ->
    "${periode.gyldig_fra} -> ${periode.gyldig_til}"
}

fun List<Samhandler>.formaterPraksis() = flatMap { it.samh_praksis }
    .joinToString(",", "praksis(", ") ") { praksis ->
        "${praksis.navn}: ${praksis.samh_praksis_status_kode} ${praksis.samh_praksis_periode.formaterPerioder()}"
    }
