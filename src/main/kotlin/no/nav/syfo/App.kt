package no.nav.syfo

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.emottak.subscription.SubscriptionPort
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.application.api.authentication.getWellKnown
import no.nav.syfo.client.StsClientProperties
import no.nav.syfo.fastlege.ws.adresseregister.adresseregisterSoapClient
import no.nav.syfo.fastlege.ws.fastlege.fastlegeSoapClient
import no.nav.syfo.ws.createPort
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

const val applicationPort = 8080

fun main() {
    val applicationState = ApplicationState(
        alive = true,
        ready = false
    )

    val server = embeddedServer(
        Netty,
        applicationEngineEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
            config = HoconApplicationConfig(ConfigFactory.load())

            val environment = Environment()

            connector {
                port = applicationPort
            }
            val wellKnown = getWellKnown(environment.azureAppWellKnownUrl)

            val stsSamlProperties = StsClientProperties(
                baseUrl = environment.stsSamlUrl,
                serviceuserUsername = environment.serviceuserUsername,
                serviceuserPassword = environment.serviceuserPassword,
            )
            val fastlegeSoapClient = fastlegeSoapClient(
                serviceUrl = environment.fastlegeUrl,
                stsProperties = stsSamlProperties,
            )
            val adresseregisterSoapClient = adresseregisterSoapClient(
                serviceUrl = environment.adresseregisterUrl,
                stsProperties = stsSamlProperties,
            )
            val subscriptionEmottak = createPort<SubscriptionPort>(environment.subscriptionEndpointURL) {
                port { withBasicAuth(environment.serviceuserUsername, environment.serviceuserPassword) }
            }

            module {
                apiModule(
                    applicationState = applicationState,
                    environment = environment,
                    wellKnown = wellKnown,
                    fastlegeSoapClient = fastlegeSoapClient,
                    adresseregisterSoapClient = adresseregisterSoapClient,
                    subscriptionPort = subscriptionEmottak,
                )
            }
        }
    )
    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.stop(10, 10, TimeUnit.SECONDS)
        }
    )

    server.environment.monitor.subscribe(ApplicationStarted) { application ->
        applicationState.ready = true
        application.environment.log.info("Application is ready")
    }
    server.start(wait = true)
}
