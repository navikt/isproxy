package no.nav.syfo.application

import no.nav.syfo.util.getFileAsString

data class Environment(
    val aadAppClient: String = getEnvVar("AZURE_APP_CLIENT_ID"),
    val azureAppWellKnownUrl: String = getEnvVar("AZURE_APP_WELL_KNOWN_URL"),
    val azureAppPreAuthorizedApps: String = getEnvVar("AZURE_APP_PRE_AUTHORIZED_APPS"),
    val serviceuserUsername: String = getFileAsString("/secrets/serviceuser/isproxy/username"),
    val serviceuserPassword: String = getFileAsString("/secrets/serviceuser/isproxy/password"),
    val stsSamlUrl: String = getEnvVar("STS_SAML_URL"),
    val fastlegeUrl: String = getEnvVar("FASTLEGE_URL"),
    val adresseregisterUrl: String = getEnvVar("ADRESSEREGISTER_URL"),
    val fastlegerestApplicationName: String = "fastlegerest",
    val fastlegeAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val fastlegepraksisAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
