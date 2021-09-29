package no.nav.syfo.fastlege.ws.adresseregister

import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.fastlege.ws.util.*
import no.nhn.register.communicationparty.ICommunicationPartyService

fun adresseregisterSoapClient(
    serviceUrl: String,
    stsProperties: StsClientProperties,
): ICommunicationPartyService {
    val port = WsClient<ICommunicationPartyService>().createPort(
        serviceUrl = serviceUrl,
        portType = ICommunicationPartyService::class.java,
        handlers = listOf(LogErrorHandler())
    )
    configureRequestSamlToken(
        port = port,
        stsProperties = stsProperties,
    )
    return port
}
