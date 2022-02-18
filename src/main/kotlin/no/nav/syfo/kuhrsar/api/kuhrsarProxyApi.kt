package no.nav.syfo.ereg.api

import io.ktor.application.*
import io.ktor.client.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.application.api.access.APIConsumerAccessService
import no.nav.syfo.btsys.*
import no.nav.syfo.domain.PersonIdent
import no.nav.syfo.kuhrsar.client.*
import no.nav.syfo.kuhrsar.model.findBestSamhandlerPraksis
import no.nav.syfo.util.*

const val kuhrsarProxyBasePath = "/api/v1/kuhrsar"

fun Route.registerKuhrsarApi(
    apiConsumerAccessService: APIConsumerAccessService,
    authorizedApplicationNameList: List<String>,
    kuhrsarClient: KuhrSarClient,
    subscriptionPort: SubscriptionPort,
) {
    route(kuhrsarProxyBasePath) {
        get() {
            proxyRequestHandler(
                apiConsumerAccessService = apiConsumerAccessService,
                authorizedApplicationNameList = authorizedApplicationNameList,
                proxyServiceName = "Kuhrsar",
            ) {
                try {
                    val kuhrsarRequest = call.receive<KuhrsarRequest>()

                    val samhandlerListe = kuhrsarClient.getSamhandler(kuhrsarRequest.behandlerIdent)
                    val samhandlerPraksis = samhandlerListe.findBestSamhandlerPraksis(
                        orgName = kuhrsarRequest.legekontorOrgName,
                        legekontorHerId = kuhrsarRequest.legekontorHerId,
                        partnerId = kuhrsarRequest.partnerId,
                        data = kuhrsarRequest.data,
                        subscriptionPort = subscriptionPort,
                    )
                    COUNT_CALL_KUHRSAR_SUCCESS.increment()
                    call.respond(KuhrsarResponse(samhandlerPraksis?.tss_ident ?: ""))
                } catch (responseException: ResponseException) {
                    COUNT_CALL_KUHRSAR_FAIL.increment()
                    throw responseException
                }
            }
        }
    }
}

data class KuhrsarRequest(
    val behandlerIdent: PersonIdent,
    val partnerId: Int?,
    val legekontorOrgName: String,
    val legekontorHerId: String?,
    val data: ByteArray,
)

data class KuhrsarResponse(
    val tssId: String,
)
