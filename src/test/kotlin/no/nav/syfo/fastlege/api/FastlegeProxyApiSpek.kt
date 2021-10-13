package no.nav.syfo.fastlege.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.fastlege.ws.fastlege.model.Fastlege
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_ADR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_NAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_POSTBOKS
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_POSTNR_STRING
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_ETTERNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_FNR
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FastlegeProxyApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(FastlegeProxyApiSpek::class.java.simpleName) {

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

            val urlFastlege = "$fastlegeBasePath"
            val fnr = FASTLEGEOPPSLAG_PERSON_ID
            val fnrNotFound = "10101012346"

            describe("Get Fastlege") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testFastlegerestClientId,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, fnr)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 1
                            val fastlege = fastleger[0]
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo FASTLEGE_FNR
                            val kontor = fastlege.fastlegekontor
                            kontor.navn shouldBeEqualTo FASTLEGEKONTOR_NAVN
                            kontor.postadresse.postnummer shouldBeEqualTo FASTLEGEKONTOR_POSTNR_STRING
                            kontor.postadresse.adresse shouldBeEqualTo FASTLEGEKONTOR_POSTBOKS
                            kontor.besoeksadresse.adresse shouldBeEqualTo FASTLEGEKONTOR_ADR
                        }
                    }
                    it("should return OK if request is successful but no result") {
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, fnrNotFound)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 0
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(NAV_PERSONIDENT_HEADER, fnr)
                            }
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
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
                                addHeader(NAV_PERSONIDENT_HEADER, fnr)
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
