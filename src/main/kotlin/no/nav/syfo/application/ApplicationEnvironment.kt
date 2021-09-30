package no.nav.syfo.application

import io.ktor.application.*
import no.nav.syfo.util.getFileAsString

data class Environment(
    val aadAppClient: String = getEnvVar("AZURE_APP_CLIENT_ID"),
    val azureAppWellKnownUrl: String = getEnvVar("AZURE_APP_WELL_KNOWN_URL"),
    val azureAppPreAuthorizedApps: String = getEnvVar("AZURE_APP_PRE_AUTHORIZED_APPS"),
    val serviceuserUsername: String = getFileAsString("/secrets/serviceuser/isproxy/username"),
    val serviceuserPassword: String = getFileAsString("/secrets/serviceuser/isproxy/password"),
    val eregUrl: String = getEnvVar("EREG_URL"),
    val stsUrl: String = getEnvVar("SECURITY_TOKEN_SERVICE_URL"),
    val axsysUrl: String = getEnvVar("AXSYS_URL"),
    val dokdistUrl: String = getEnvVar("DOKDIST_URL"),
    val fastlegeUrl: String = getEnvVar("FASTLEGE_URL"),
    val adresseregisterUrl: String = getEnvVar("ADRESSEREGISTER_URL"),
    val syfosyketilfelleUrl: String = getEnvVar("SYFOSYKETILFELLE_URL"),

    val isdialogmoteApplicationName: String = "isdialogmote",
    val isnarmestelederApplicationName: String = "isnarmesteleder",
    val fastlegerestApplicationName: String = "fastlegerest",
    val syfoveilederApplicationName: String = "syfoveileder",
    val eregAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        isnarmestelederApplicationName,
    ),
    val axsysAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        syfoveilederApplicationName,
    ),
    val dokdistAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        isdialogmoteApplicationName,
    ),
    val fastlegeAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val fastlegepraksisAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val syfosyketilfelleAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        isdialogmoteApplicationName,
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
