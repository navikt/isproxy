package no.nav.syfo.dkif.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.dkif.client.DkifClient
import no.nav.syfo.dkif.domain.DigitalKontaktinfoBolk
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DkifApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(DkifApiSpek::class.java.simpleName) {

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

            val urlDkifKontaktinformasjon = "$dkifProxyBasePath$dkifProxyKontaktinformasjonPath"

            describe("Get Kontaktinformasjon from Dkif") {
                describe("Happy path") {
                    it("Isdialogmote: should return OK if request is successful") {
                        val validTokenIsdialogmote = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testIsdialogmoteClientId,
                        )
                        with(
                            handleRequest(HttpMethod.Get, urlDkifKontaktinformasjon) {
                                addHeader(Authorization, bearerHeader(validTokenIsdialogmote))
                                addHeader(DkifClient.NAV_PERSONIDENTER_HEADER, ARBEIDSTAKER_PERSONIDENT)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val dkifResponse = objectMapper.readValue<DigitalKontaktinfoBolk>(response.content!!)
                            dkifResponse shouldBeEqualTo externalMockEnvironment.dkifMock.dkifResponse
                        }
                    }

                    it("Syfoperson: should return OK if request is successful") {
                        val validTokenSyfoperson = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testSyfopersonClientId,
                        )
                        with(
                            handleRequest(HttpMethod.Get, urlDkifKontaktinformasjon) {
                                addHeader(Authorization, bearerHeader(validTokenSyfoperson))
                                addHeader(DkifClient.NAV_PERSONIDENTER_HEADER, ARBEIDSTAKER_PERSONIDENT)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val dkifResponse = objectMapper.readValue<DigitalKontaktinfoBolk>(response.content!!)
                            dkifResponse shouldBeEqualTo externalMockEnvironment.dkifMock.dkifResponse
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlDkifKontaktinformasjon) {}
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }

                    it("should return status Forbidden if unauthorized AZP is supplied") {
                        val validTokenUnauthorizedAZP = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testSyfoveilederClientId,
                        )

                        with(
                            handleRequest(HttpMethod.Get, urlDkifKontaktinformasjon) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
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
