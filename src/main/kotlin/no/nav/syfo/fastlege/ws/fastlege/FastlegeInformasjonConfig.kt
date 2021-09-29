package no.nav.syfo.fastlege.ws.fastlege

import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.fastlege.ws.util.*
import no.nhn.schemas.reg.flr.IFlrReadOperations

fun fastlegeSoapClient(
    serviceUrl: String,
    stsProperties: StsClientProperties,
): IFlrReadOperations {
    val port = WsClient<IFlrReadOperations>().createPort(
        serviceUrl = serviceUrl,
        portType = IFlrReadOperations::class.java,
        handlers = listOf(LogErrorHandler())
    )
    configureRequestSamlToken(
        port = port,
        stsProperties = stsProperties,
    )
    return port
}
