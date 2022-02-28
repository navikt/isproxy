package no.nav.syfo.application

import no.nav.syfo.util.getFileAsString

data class Environment(
    val aadAppClient: String = getEnvVar("AZURE_APP_CLIENT_ID"),
    val aadAppSecret: String = getEnvVar("AZURE_APP_CLIENT_SECRET"),
    val aadTokenEndpoint: String = getEnvVar("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"),
    val azureAppWellKnownUrl: String = getEnvVar("AZURE_APP_WELL_KNOWN_URL"),
    val azureAppPreAuthorizedApps: String = getEnvVar("AZURE_APP_PRE_AUTHORIZED_APPS"),
    val btsysUrl: String = getEnvVar("BTSYS_URL"),
    val serviceuserUsername: String = getFileAsString("/secrets/serviceuser/isproxy/username"),
    val serviceuserPassword: String = getFileAsString("/secrets/serviceuser/isproxy/password"),
    val eregUrl: String = getEnvVar("EREG_URL"),
    val stsUrl: String = getEnvVar("SECURITY_TOKEN_SERVICE_URL"),
    val stsSamlUrl: String = getEnvVar("STS_SAML_URL"),
    val axsysUrl: String = getEnvVar("AXSYS_URL"),
    val fastlegeUrl: String = getEnvVar("FASTLEGE_URL"),
    val adresseregisterUrl: String = getEnvVar("ADRESSEREGISTER_URL"),
    val kuhrsarClientId: String = getEnvVar("KUHRSAR_CLIENT_ID"),
    val kuhrsarUrl: String = getEnvVar("KUHRSAR_URL"),
    val norg2Url: String = getEnvVar("NORG2_URL"),
    val subscriptionEndpointURL: String = getEnvVar("SUBSCRIPTION_ENDPOINT_URL"),

    val isdialogmoteApplicationName: String = "isdialogmote",
    val isnarmestelederApplicationName: String = "isnarmesteleder",
    val fastlegerestApplicationName: String = "fastlegerest",
    val padm2ApplicationName: String = "padm2",
    val syfobehandlendeenhetApplicationName: String = "syfobehandlendeenhet",
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
    val fastlegeAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val norg2APIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        syfobehandlendeenhetApplicationName,
    ),
    val fastlegepraksisAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        fastlegerestApplicationName,
    ),
    val kuhrsarAPIAuthorizedConsumerApplicationNameList: List<String> = listOf(
        padm2ApplicationName,
    ),
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
