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
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_ETTERNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_FNR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_HPR_NR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_RELASJON_KODETEKST
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_RELASJON_KODEVERDI
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_STILLINGSPROSENT
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_ETTERNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_FNR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_HPR_NR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_RELASJON_KODETEKST
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_RELASJON_KODEVERDI
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_STILLINGSPROSENT
import no.nav.syfo.testhelper.UserConstants.HER_ID
import no.nav.syfo.util.*
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FastlegeProxyApiSpek : Spek({
    val objectMapper: ObjectMapper = configuredJacksonMapper()

    describe(FastlegeProxyApiSpek::class.java.simpleName) {

        with(TestApplicationEngine()) {
            start()

            val externalMockEnvironment = ExternalMockEnvironment()

            application.testApiModule(
                externalMockEnvironment = externalMockEnvironment,
            )

            val urlFastlege = fastlegeBasePath
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
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 2

                            val fastlege = fastleger.first()
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo FASTLEGE_FNR
                            val kontor = fastlege.fastlegekontor
                            kontor.navn shouldBeEqualTo FASTLEGEKONTOR_NAVN
                            kontor.postadresse!!.postnummer shouldBeEqualTo FASTLEGEKONTOR_POSTNR_STRING
                            kontor.postadresse!!.adresse shouldBeEqualTo FASTLEGEKONTOR_POSTBOKS
                            kontor.besoeksadresse!!.adresse shouldBeEqualTo FASTLEGEKONTOR_ADR

                            val fastlegeVikar = fastleger.last()
                            fastlegeVikar.etternavn shouldBeEqualTo FASTLEGE_VIKAR_ETTERNAVN
                            fastlegeVikar.fnr shouldBeEqualTo FASTLEGE_VIKAR_FNR
                            val kontorFastlegeVikar = fastlegeVikar.fastlegekontor
                            kontorFastlegeVikar.navn shouldBeEqualTo FASTLEGEKONTOR_NAVN
                            kontorFastlegeVikar.postadresse!!.postnummer shouldBeEqualTo FASTLEGEKONTOR_POSTNR_STRING
                            kontorFastlegeVikar.postadresse!!.adresse shouldBeEqualTo FASTLEGEKONTOR_POSTBOKS
                            kontorFastlegeVikar.besoeksadresse!!.adresse shouldBeEqualTo FASTLEGEKONTOR_ADR
                        }
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 2

                            val fastlege = fastleger.first()
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo FASTLEGE_FNR
                            fastlege.helsepersonellregisterId shouldBeEqualTo null
                            fastlege.herId shouldBeEqualTo HER_ID

                            val fastlegeVikar = fastleger.last()
                            fastlegeVikar.etternavn shouldBeEqualTo FASTLEGE_VIKAR_ETTERNAVN
                            fastlegeVikar.fnr shouldBeEqualTo FASTLEGE_VIKAR_FNR
                            fastlegeVikar.helsepersonellregisterId shouldBeEqualTo null
                            fastlegeVikar.herId shouldBeEqualTo HER_ID
                        }
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 2

                            val fastlege = fastleger.first()
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo FASTLEGE_FNR
                            fastlege.helsepersonellregisterId shouldBeEqualTo FASTLEGE_HPR_NR
                            fastlege.herId shouldBeEqualTo null
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo FASTLEGE_RELASJON_KODEVERDI
                            fastlege.relasjon.kodeTekst shouldBeEqualTo FASTLEGE_RELASJON_KODETEKST
                            fastlege.stillingsprosent shouldBeEqualTo FASTLEGE_STILLINGSPROSENT

                            val fastlegeVikar = fastleger.last()
                            fastlegeVikar.etternavn shouldBeEqualTo FASTLEGE_VIKAR_ETTERNAVN
                            fastlegeVikar.fnr shouldBeEqualTo FASTLEGE_VIKAR_FNR
                            fastlegeVikar.helsepersonellregisterId shouldBeEqualTo FASTLEGE_VIKAR_HPR_NR
                            fastlegeVikar.herId shouldBeEqualTo null
                            fastlegeVikar.relasjon.kodeVerdi shouldBeEqualTo FASTLEGE_VIKAR_RELASJON_KODEVERDI
                            fastlegeVikar.relasjon.kodeTekst shouldBeEqualTo FASTLEGE_VIKAR_RELASJON_KODETEKST
                            fastlegeVikar.stillingsprosent shouldBeEqualTo FASTLEGE_VIKAR_STILLINGSPROSENT
                        }
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 2

                            val fastlege = fastleger.first()
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo FASTLEGE_FNR
                            fastlege.fastlegekontor.postadresse shouldBeEqualTo null
                            fastlege.fastlegekontor.besoeksadresse shouldNotBeEqualTo null

                            val fastlegeVikar = fastleger.last()
                            fastlegeVikar.etternavn shouldBeEqualTo FASTLEGE_VIKAR_ETTERNAVN
                            fastlegeVikar.fnr shouldBeEqualTo FASTLEGE_VIKAR_FNR
                            fastlegeVikar.fastlegekontor.postadresse shouldBeEqualTo null
                            fastlegeVikar.fastlegekontor.besoeksadresse shouldNotBeEqualTo null
                        }
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 2

                            val fastlege = fastleger.first()
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo FASTLEGE_FNR
                            fastlege.fastlegekontor.postadresse shouldNotBeEqualTo null
                            fastlege.fastlegekontor.besoeksadresse shouldBeEqualTo null

                            val fastlegeVikar = fastleger.last()
                            fastlegeVikar.etternavn shouldBeEqualTo FASTLEGE_VIKAR_ETTERNAVN
                            fastlegeVikar.fnr shouldBeEqualTo FASTLEGE_VIKAR_FNR
                            fastlegeVikar.fastlegekontor.postadresse shouldNotBeEqualTo null
                            fastlegeVikar.fastlegekontor.besoeksadresse shouldBeEqualTo null
                        }
                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastleger = objectMapper.readValue<List<Fastlege>>(response.content!!)
                            fastleger.size shouldBeEqualTo 2

                            val fastlege = fastleger.first()
                            fastlege.etternavn shouldBeEqualTo FASTLEGE_ETTERNAVN
                            fastlege.fnr shouldBeEqualTo null
                            fastlege.fastlegekontor.postadresse shouldNotBeEqualTo null
                            fastlege.fastlegekontor.besoeksadresse shouldNotBeEqualTo null

                            val fastlegeVikar = fastleger.last()
                            fastlegeVikar.etternavn shouldBeEqualTo FASTLEGE_VIKAR_ETTERNAVN
                            fastlegeVikar.fnr shouldBeEqualTo null
                            fastlegeVikar.fastlegekontor.postadresse shouldNotBeEqualTo null
                            fastlegeVikar.fastlegekontor.besoeksadresse shouldNotBeEqualTo null
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
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }

                    it("should return status Forbidden if unauthorized AZP is supplied") {
                        val validTokenUnauthorizedAZP = generateJWT(
                            externalMockEnvironment.environment.aadAppClient,
                            externalMockEnvironment.wellKnown.issuer,
                            "unauthorizedAzpClientId",
                        )

                        with(
                            handleRequest(HttpMethod.Get, urlFastlege) {
                                addHeader(Authorization, bearerHeader(validTokenUnauthorizedAZP))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
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
