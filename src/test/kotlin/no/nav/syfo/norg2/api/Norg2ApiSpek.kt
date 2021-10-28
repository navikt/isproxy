package no.nav.syfo.norg2.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.server.testing.*
import no.nav.syfo.norg2.domain.*
import no.nav.syfo.testhelper.*
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class Norg2ApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(Norg2ApiSpek::class.java.simpleName) {

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

            val url = "$norg2ProxyBasePath$norg2ProxyArbeidsfordelingBestmatchPath"

            describe("Get list of NorgEnhet from Norg2") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                    testSyfobehandlendeenhetClientId,
                )

                val arbeidsfordelingCriteria = ArbeidsfordelingCriteria(
                    diskresjonskode = null,
                    behandlingstype = "ae0257",
                    tema = "OPP",
                    geografiskOmraade = "93140",
                    skjermet = false,
                )
                val requestBody = objectMapper.writeValueAsString(arbeidsfordelingCriteria)

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Post, url) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(ContentType, io.ktor.http.ContentType.Application.Json.toString())
                                setBody(requestBody)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val norgEnhetList: List<NorgEnhet> = objectMapper.readValue(response.content!!)

                            norgEnhetList shouldBeEqualTo externalMockEnvironment.norg2Mock.norg2Response
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Post, url) {}
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
                            handleRequest(HttpMethod.Post, url) {
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
