package no.nav.syfo.testhelper.mock

import no.nav.emottak.subscription.*

class SubscriptionMock : SubscriptionPort {
    override fun startSubscription(startSubscriptionRequest: StartSubscriptionRequest?): StatusResponse {
        return StatusResponse()
    }

    override fun finnElektroniskSamhandlerstatus(finnElektroniskSamhandlerstatusRequest: FinnElektroniskSamhandlerstatusRequest?): FinnElektroniskSamhandlerstatusResponse {
        return FinnElektroniskSamhandlerstatusResponse()
    }
}
