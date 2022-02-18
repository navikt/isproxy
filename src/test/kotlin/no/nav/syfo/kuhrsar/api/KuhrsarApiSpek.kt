package no.nav.syfo.kuhrsar.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import io.mockk.*
import no.nav.emottak.subscription.*
import no.nav.syfo.domain.PersonIdent
import no.nav.syfo.ereg.api.*
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_PRAKSIS_NAME
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class KuhrsarApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(KuhrsarApiSpek::class.java.simpleName) {

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

            val urlKuhrsar = kuhrsarProxyBasePath
            val subscriptionMock = externalMockEnvironment.subscriptionMock
            clearSubscriptionMock(subscriptionMock)

            describe("Find samhandler") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testPadm2ClientId,
                )
                val kuhrsarRequest = KuhrsarRequest(
                    behandlerIdent = PersonIdent("01010112345"),
                    partnerId = 1,
                    legekontorOrgName = "navn",
                    legekontorHerId = "herid",
                    data = "data".toByteArray(),
                )
                describe("Happy path") {

                    it("test that returns OK if request is successful, but samhandler not found") {
                        val requestBody = objectMapper.writeValueAsString(kuhrsarRequest)
                        with(
                            handleRequest(HttpMethod.Get, urlKuhrsar) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val response = objectMapper.readValue<KuhrsarResponse>(response.content!!)
                            response.tssId shouldBeEqualTo ""
                            verify(exactly = 0) { subscriptionMock.startSubscription(any()) }
                        }
                    }
                    it("test that returns OK if request is successful and samhandler found") {
                        val kuhrsarRequestFoundByHerId = KuhrsarRequest(
                            behandlerIdent = PersonIdent(FASTLEGEOPPSLAG_PERSON_ID),
                            partnerId = 1,
                            legekontorOrgName = "Kule helsetjenester",
                            legekontorHerId = "${UserConstants.HER_ID}",
                            data = "data".toByteArray(),
                        )
                        val requestBody = objectMapper.writeValueAsString(kuhrsarRequestFoundByHerId)
                        with(
                            handleRequest(HttpMethod.Get, urlKuhrsar) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val response = objectMapper.readValue<KuhrsarResponse>(response.content!!)
                            response.tssId shouldBeEqualTo "123"
                            verify(exactly = 1) { subscriptionMock.startSubscription(any()) }
                            clearSubscriptionMock(subscriptionMock)
                        }
                    }
                    it("test that returns OK if request is successful and samhandler found by name") {
                        val kuhrsarRequestFoundByOrgName = KuhrsarRequest(
                            behandlerIdent = PersonIdent(FASTLEGEOPPSLAG_PERSON_ID),
                            partnerId = 1,
                            legekontorOrgName = "Kule helsetjenester",
                            legekontorHerId = "herId",
                            data = "data".toByteArray(),
                        )
                        val requestBody = objectMapper.writeValueAsString(kuhrsarRequestFoundByOrgName)
                        with(
                            handleRequest(HttpMethod.Get, urlKuhrsar) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val response = objectMapper.readValue<KuhrsarResponse>(response.content!!)
                            response.tssId shouldBeEqualTo "123"
                            verify(exactly = 1) { subscriptionMock.startSubscription(any()) }
                            clearSubscriptionMock(subscriptionMock)
                        }
                    }
                    it("test that returns OK if request is successful and Fastlege-samhandler found") {
                        val kuhrsarRequestFoundByOrgName = KuhrsarRequest(
                            behandlerIdent = PersonIdent(FASTLEGEOPPSLAG_PERSON_ID_MISSING_PRAKSIS_NAME),
                            partnerId = 1,
                            legekontorOrgName = "Kule helsetjenester",
                            legekontorHerId = "herId",
                            data = "data".toByteArray(),
                        )
                        val requestBody = objectMapper.writeValueAsString(kuhrsarRequestFoundByOrgName)
                        with(
                            handleRequest(HttpMethod.Get, urlKuhrsar) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val response = objectMapper.readValue<KuhrsarResponse>(response.content!!)
                            response.tssId shouldBeEqualTo "1234"
                            verify(exactly = 0) { subscriptionMock.startSubscription(any()) }
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("test that returns status Unauthorized if no token is supplied") {
                        val requestBody = objectMapper.writeValueAsString(kuhrsarRequest)
                        with(
                            handleRequest(HttpMethod.Get, urlKuhrsar) {
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }

                    it("test that returns status Forbidden if unauthorized AZP is supplied") {
                        val validTokenUnauthorizedAZP = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testSyfopersonClientId,
                        )
                        val requestBody = objectMapper.writeValueAsString(kuhrsarRequest)

                        with(
                            handleRequest(HttpMethod.Get, urlKuhrsar) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Forbidden
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
