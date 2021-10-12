package no.nav.syfo.fastlege.ws.util

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import java.util.*
import javax.xml.ws.BindingProvider
import javax.xml.ws.handler.Handler

class WsClient<T> {
    fun createPort(
        serviceUrl: String,
        portType: Class<*>,
        handlers: List<Handler<*>>,
    ): T {
        val jaxWsProxyFactoryBean = JaxWsProxyFactoryBean()
        jaxWsProxyFactoryBean.serviceClass = portType
        jaxWsProxyFactoryBean.address = Objects.requireNonNull(serviceUrl)
        val port = jaxWsProxyFactoryBean.create() as T
        (port as BindingProvider).binding.handlerChain = handlers
        return port
    }
}
