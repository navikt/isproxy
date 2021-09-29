package no.nav.syfo.fastlege.ws.util

import no.nav.syfo.client.StsClientProperties
import org.apache.cxf.Bus
import org.apache.cxf.binding.soap.Soap12
import org.apache.cxf.binding.soap.SoapMessage
import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.rt.security.SecurityConstants
import org.apache.cxf.ws.policy.PolicyBuilder
import org.apache.cxf.ws.policy.PolicyEngine
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver
import org.apache.cxf.ws.security.trust.STSClient
import org.apache.neethi.Policy
import java.util.*

private const val STS_PATH = "/SecurityTokenServiceProvider/"
private const val STS_REQUEST_SAML_POLICY = "classpath:policy/requestSamlPolicy.xml"
private const val STS_CLIENT_AUTHENTICATION_POLICY = "classpath:policy/untPolicy.xml"

fun configureRequestSamlToken(
    port: Any,
    stsProperties: StsClientProperties,
) {
    val client = ClientProxy.getClient(port)
    val stsClient = createCustomSTSClient(client.bus)
    configureSTSClient(
        stsClient = stsClient,
        location = stsProperties.baseUrl + STS_PATH,
        username = stsProperties.serviceuserUsername,
        password = stsProperties.serviceuserPassword,
    )
    client.requestContext[SecurityConstants.STS_CLIENT] = stsClient
    client.requestContext[SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT] = true
    setEndpointPolicyReference(client, STS_REQUEST_SAML_POLICY)
}

fun createCustomSTSClient(bus: Bus): STSClient {
    return STSClientWSTrust13and14(bus)
}

fun configureSTSClient(
    stsClient: STSClient,
    location: String?,
    username: String,
    password: String
) {
    stsClient.isEnableAppliesTo = false
    stsClient.isAllowRenewing = false
    stsClient.location = location
    val properties = HashMap<String, Any>()
    properties[SecurityConstants.USERNAME] = username
    properties[SecurityConstants.PASSWORD] = password
    stsClient.properties = properties
    stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY)
}

fun setEndpointPolicyReference(
    client: Client,
    uri: String
) {
    val policy = resolvePolicyReference(client, uri)
    setClientEndpointPolicy(client, policy)
}

private fun resolvePolicyReference(
    client: Client,
    uri: String
): Policy {
    val policyBuilder = client.bus.getExtension(PolicyBuilder::class.java)
    val resolver: ReferenceResolver = RemoteReferenceResolver("", policyBuilder)
    return resolver.resolveReference(uri)
}

private fun setClientEndpointPolicy(
    client: Client,
    policy: Policy
) {
    val endpoint = client.endpoint
    val endpointInfo = endpoint.endpointInfo
    val policyEngine = client.bus.getExtension(PolicyEngine::class.java)
    val message = SoapMessage(Soap12.getInstance())
    val endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message)
    policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message))
}
