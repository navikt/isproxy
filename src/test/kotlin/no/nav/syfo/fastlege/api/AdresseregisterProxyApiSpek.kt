package no.nav.syfo.fastlege.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.fastlege.ws.adresseregister.PraksisInfo
import no.nav.syfo.testhelper.*
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class AdresseregisterProxyApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(AdresseregisterProxyApiSpek::class.java.simpleName) {

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

            val urlFastlegepraksis = "$fastlegepraksisBasePath/${UserConstants.HER_ID}"
            val urlFastlegepraksisNotFound = "$fastlegepraksisBasePath/999"

            describe("Get Fastlegepraksis") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testFastlegerestClientId,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlFastlegepraksis) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val praksisInfo = objectMapper.readValue<PraksisInfo>(response.content!!)
                            praksisInfo.foreldreEnhetHerId shouldBeEqualTo UserConstants.PARENT_HER_ID
                        }
                    }
                    it("should return Not Found if request is successful but no result") {
                        with(
                            handleRequest(HttpMethod.Get, urlFastlegepraksisNotFound) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NotFound
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlFastlegepraksis)
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }

                    it("should return status Forbidden if unauthorized AZP is supplied") {
                        val validTokenUnauthorizedAZP = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testIsdialogmoteClientId,
                        )

                        with(
                            handleRequest(HttpMethod.Get, urlFastlegepraksis) {
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
