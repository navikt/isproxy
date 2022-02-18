package no.nav.syfo.subscription.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import io.mockk.*
import no.nav.emottak.subscription.StatusResponse
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.ereg.api.SubscriptionRequest
import no.nav.syfo.ereg.api.subscriptionProxyBasePath
import no.nav.syfo.testhelper.*
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SubscriptionApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(SubscriptionApiSpek::class.java.simpleName) {

        with(TestApplicationEngine()) {
            start()

            val externalMockEnvironment = ExternalMockEnvironment()

            beforeGroup {
                externalMockEnvironment.startExternalMocks()
            }

            afterGroup {
                externalMockEnvironment.stopExternalMocks()
            }

            application.testApiModule(
                externalMockEnvironment = externalMockEnvironment,
            )

            val urlSubscription = subscriptionProxyBasePath

            describe("Start subscription") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testPadm2ClientId,
                )
                val subscripionRequest = SubscriptionRequest(
                    tssIdent = "tss",
                    partnerId = 0,
                    data = "data".toByteArray()
                )
                val requestBody = objectMapper.writeValueAsString(subscripionRequest)
                val subscriptionMock = externalMockEnvironment.subscriptionMock
                clearSubscriptionMock(subscriptionMock)

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Post, urlSubscription) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            verify(exactly = 1) { subscriptionMock.startSubscription(any()) }
                            clearSubscriptionMock(subscriptionMock)
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Post, urlSubscription) {
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                            verify(exactly = 0) { subscriptionMock.startSubscription(any()) }
                        }
                    }

                    it("should return status Forbidden if unauthorized AZP is supplied") {
                        val validTokenUnauthorizedAZP = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testIsdialogmoteClientId,
                        )

                        with(
                            handleRequest(HttpMethod.Post, urlSubscription) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Forbidden
                            verify(exactly = 0) { subscriptionMock.startSubscription(any()) }
                        }
                    }
                }
            }
        }
    }
})

private fun clearSubscriptionMock(subscriptionMock: SubscriptionPort) {
    clearMocks(subscriptionMock)
    every { subscriptionMock.startSubscription(any()) } returns (StatusResponse())
}
