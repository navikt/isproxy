package no.nav.syfo.fastlege.ws.util

import org.apache.cxf.Bus
import org.apache.cxf.ws.security.trust.STSClient

class STSClientWSTrust13and14(b: Bus) : STSClient(b) {

    override fun useSecondaryParameters(): Boolean {
        return false
    }
}
