package no.nav.syfo.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.client.dokdist.DokdistRequest
import no.nav.syfo.client.dokdist.DokdistResponse
import no.nav.syfo.dokdist.api.distribuerJournalpostPath
import no.nav.syfo.dokdist.api.dokDistBasePath
import no.nav.syfo.testhelper.*
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DistribuerJournalpostApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(DistribuerJournalpostApiSpek::class.java.simpleName) {

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

            val urlDistribuerJournalpost = "$dokDistBasePath/$distribuerJournalpostPath"

            describe("Post distribuer journalpost") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                )
                val dokdistRequest = DokdistRequest(
                    journalpostId = "jpid",
                    bestillendeFagsystem = "isdialogmote",
                    dokumentProdApp = "isdialogmote",
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Post, urlDistribuerJournalpost) {
                                addHeader(Authorization, bearerHeader(validToken))
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(objectMapper.writeValueAsString(dokdistRequest))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val dokDistResponse = objectMapper.readValue<DokdistResponse>(response.content!!)
                            dokDistResponse.bestillingsId shouldBeEqualTo "bestillings_id_fra_dokdist"
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Post, urlDistribuerJournalpost) {
                                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                setBody(objectMapper.writeValueAsString(dokdistRequest))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }
                }
            }
        }
    }
})
