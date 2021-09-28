package no.nav.syfo.syfosyketilfelle.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.syfosyketilfelle.domain.KOppfolgingstilfellePerson
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SyfosyketilfelleApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(SyfosyketilfelleApiSpek::class.java.simpleName) {

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

            val urlOppfolgingstilfellePersonWithParam =
                "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_AKTORID.value}"

            describe("Get OppfolgingstilfellePerson from Syfosyketilfelle") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testIsdialogmoteClientId,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonWithParam) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val oppfolgingstilfellePersonResponse =
                                objectMapper.readValue<KOppfolgingstilfellePerson>(response.content!!)
                            oppfolgingstilfellePersonResponse shouldBeEqualTo externalMockEnvironment.syfosyketilfelleMock.oppfolgingstilfellePersonResponse
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status NoContent if Person has no Oppfolgingstilfelle") {
                        val urlOppfolgingstilfellePersonNoOppfolgingstilfelle =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID.value}"

                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonNoOppfolgingstilfelle) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NoContent
                        }
                    }

                    it("should return the same error status as Syfosyketilfelle") {
                        val urlOppfolgingstilfellePersonNoOppfolgingstilfelle =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${UserConstants.ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}"

                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonNoOppfolgingstilfelle) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest
                        }
                    }

                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonWithParam) {}
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }

                    it("should return status Forbidden if unauthorized AZP is supplied") {
                        val validTokenUnauthorizedAZP = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            testIsnarmestelederClientId,
                        )

                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonWithParam) {
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
