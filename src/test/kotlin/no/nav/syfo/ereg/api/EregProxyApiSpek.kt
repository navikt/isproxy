package no.nav.syfo.ereg.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.server.testing.*
import no.nav.syfo.ereg.client.EregClient
import no.nav.syfo.ereg.domain.EregOrganisasjonResponse
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.mock.EregResponseOrgNr
import no.nav.syfo.util.bearerHeader
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EregProxyApiSpek : Spek({
    val objectMapper: ObjectMapper = apiConsumerObjectMapper()

    describe(EregProxyApiSpek::class.java.simpleName) {

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

            val urlEregOrganisasjonWithParam = "$eregProxyBasePath/${EregClient.EREG_PATH}/$EregResponseOrgNr"

            describe("Get Organisasjon from Ereg") {
                val validToken = generateJWT(
                    externalMockEnvironment.environment.aadAppClient,
                    externalMockEnvironment.wellKnown.issuer,
                )

                describe("Happy path") {

                    it("should return OK if request is successful") {
                        with(
                            handleRequest(HttpMethod.Get, urlEregOrganisasjonWithParam) {
                                addHeader(Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val eregOrganisasjonResponse = objectMapper.readValue<EregOrganisasjonResponse>(response.content!!)
                            eregOrganisasjonResponse.navn.navnelinje1 shouldBeEqualTo externalMockEnvironment.eregMock.eregResponse.navn.navnelinje1
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("should return status Unauthorized if no token is supplied") {
                        with(
                            handleRequest(HttpMethod.Get, urlEregOrganisasjonWithParam) {}
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Unauthorized
                        }
                    }
                }
            }
        }
    }
})