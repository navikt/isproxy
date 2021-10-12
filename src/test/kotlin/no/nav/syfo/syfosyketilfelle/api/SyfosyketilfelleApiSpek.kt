package no.nav.syfo.syfosyketilfelle.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.syfosyketilfelle.domain.KOppfolgingstilfelle
import no.nav.syfo.syfosyketilfelle.domain.KOppfolgingstilfellePerson
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR
import no.nav.syfo.testhelper.UserConstants.VIRKSOMHETSNUMMER_DEFAULT
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

            describe("Get OppfolgingstilfellePerson for person from Syfosyketilfelle") {
                val urlOppfolgingstilfellePerson =
                    "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_AKTORID.value}"

                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testIsdialogmoteClientId,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePerson) {
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
                        val urlOppfolgingstilfellePersonError =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}"

                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonError) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest
                        }
                    }

                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePerson) {}
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
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePerson) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Forbidden
                        }
                    }
                }
            }

            describe("Get OppfolgingstilfellePerson for Person without Arbeidsgiver/Virksomhet from Syfosyketilfelle") {
                val urlOppfolgingstilfellePersonNoArbeidsgiver =
                    "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_AKTORID.value}$syfosyketilfelleProxyOppfolgingstilfellePersonUtenArbeidsgiverPath"

                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testIsdialogmoteClientId,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonNoArbeidsgiver) {
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
                        val urlOppfolgingstilfellePersonNoArbeidsgiverNoOppfolgingstilfelle =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID.value}$syfosyketilfelleProxyOppfolgingstilfellePersonUtenArbeidsgiverPath"

                        with(
                            handleRequest(
                                HttpMethod.Get,
                                urlOppfolgingstilfellePersonNoArbeidsgiverNoOppfolgingstilfelle
                            ) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NoContent
                        }
                    }

                    it("should return the same error status as Syfosyketilfelle") {
                        val urlOppfolgingstilfellePersonNoArbeidsgiverError =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}$syfosyketilfelleProxyOppfolgingstilfellePersonUtenArbeidsgiverPath"

                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonNoArbeidsgiverError) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest
                        }
                    }

                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonNoArbeidsgiver) {}
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
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonNoArbeidsgiver) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Forbidden
                        }
                    }
                }
            }

            describe("Get Oppfolgingstilfelle for Person and Virksomhet from Syfosyketilfelle") {
                val urlOppfolgingstilfellePersonVirksomhet =
                    "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_AKTORID.value}/${VIRKSOMHETSNUMMER_DEFAULT.value}"

                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testIsdialogmoteClientId,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonVirksomhet) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val oppfolgingstilfellePersonVirksomhetResponse =
                                objectMapper.readValue<KOppfolgingstilfelle>(response.content!!)
                            oppfolgingstilfellePersonVirksomhetResponse shouldBeEqualTo externalMockEnvironment.syfosyketilfelleMock.oppfolgingstilfelleResponse
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status NoContent if Person has no Oppfolgingstilfelle") {
                        val urlOppfolgingstilfellePersonVirksomhetNoOppfolgingstilfelle =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_NO_OPPFOLGINGSTILFELLE_AKTORID.value}/${VIRKSOMHETSNUMMER_DEFAULT.value}"
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonVirksomhetNoOppfolgingstilfelle) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NoContent
                        }
                    }

                    it("should return the same error status as Syfosyketilfelle") {
                        val urlOppfolgingstilfellePersonVirksomhetError =
                            "$syfosyktilfelleProxyBasePath$syfosyketilfelleProxyOppfolgingstilfellePersonPath/${ARBEIDSTAKER_OPPFOLGINGSTILFELLE_ERROR.value}"

                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonVirksomhetError) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest
                        }
                    }

                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonVirksomhet) {}
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
                            handleRequest(HttpMethod.Get, urlOppfolgingstilfellePersonVirksomhet) {
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
