package no.nav.syfo.application

import io.ktor.application.*
import no.nav.syfo.util.getFileAsString

data class Environment(
    val aadAppClient: String = getEnvVar("AZURE_APP_CLIENT_ID"),
    val azureAppWellKnownUrl: String = getEnvVar("AZURE_APP_WELL_KNOWN_URL"),
    val azureAppPreAuthorizedApps: String = getEnvVar("AZURE_APP_PRE_AUTHORIZED_APPS"),
    val btsysUrl: String = getEnvVar("BTSYS_URL"),
    val serviceuserUsername: String = getFileAsString("/secrets/serviceuser/isproxy/username"),
    val serviceuserPassword: String = getFileAsString("/secrets/serviceuser/isproxy/password"),
    val eregUrl: String = getEnvVar("EREG_URL"),
    val stsUrl: String = getEnvVar("SECURITY_TOKEN_SERVICE_URL"),
    val stsSamlUrl: String = getEnvVar("STS_SAML_URL"),
    val axsysUrl: String = getEnvVar("AXSYS_URL"),
    val dokdistUrl: String = getEnvVar("DOKDIST_URL"),
    val fastlegeUrl: String = getEnvVar("FASTLEGE_URL"),
    val adresseregisterUrl: String = getEnvVar("ADRESSEREGISTER_URL"),
    val norg2Url: String = getEnvVar("NORG2_URL"),
    val syfosyketilfelleUrl: String = getEnvVar("SYFOSYKETILFELLE_URL"),

    val isdialogmoteApplicationName: String = "isdialogmote",
    val isnarmestelederApplicationName: String = "isnarmesteleder",
    val fastlegerestApplicationName: String = "fastlegerest",
    val padm2ApplicationName: String = "padm2",
    val syfobehandlendeenhetApplicationName: String = "syfobehandlendeenhet",
    val syfopersonApplicationName: String = "syfoperson",
    val syfoveilederApplicationName: String = "syfoveileder",
    val eregAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        isdialogmoteApplicationName,
        isnarmestelederApplicationName,
    ),
    val axsysAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        syfoveilederApplicationName,
    ),
    val btsysAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        padm2ApplicationName,
    ),
    val dokdistAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        isdialogmoteApplicationName,
    ),
    val fastlegeAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val norg2APIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        syfobehandlendeenhetApplicationName,
    ),
    val fastlegepraksisAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val syfosyketilfelleAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        isdialogmoteApplicationName,
        syfopersonApplicationName,
    ),
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")

val Application.envKind get() = environment.config.property("ktor.environment").getString()

fun Application.isDev(block: () -> Unit) {
    if (envKind == "dev") block()
}

fun Application.isProd(block: () -> Unit) {
    if (envKind == "production") block()
}
