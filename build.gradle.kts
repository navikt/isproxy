import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

object Versions {
    const val jackson = "2.13.0"
    const val jedis = "3.7.0"
    const val ktor = "1.6.6"
    const val jaxb = "2.3.1"
    const val kluent = "1.68"
    const val cxf = "3.3.9"
    const val javaxActivation = "1.2.0"
    const val javaxWsRsApi = "2.1.1"
    const val jaxws = "2.3.5"
    const val logback = "1.2.7"
    const val logstashEncoder = "7.0.1"
    const val mockk = "1.12.1"
    const val nimbusJoseJwt = "9.15.2"
    const val spek = "2.0.17"
    const val micrometerRegistry = "1.8.0"
    const val syfotjenester = "1.2021.06.09-13.09-b3d30de9996e"
}

plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

val githubUser: String by project
val githubPassword: String by project
repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
    maven(url = "https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.github.com/navikt/tjenestespesifikasjoner")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-auth-jwt:${Versions.ktor}")
    implementation("io.ktor:ktor-client-apache:${Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
    implementation("io.ktor:ktor-client-jackson:${Versions.ktor}")
    implementation("io.ktor:ktor-jackson:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")

    implementation("org.apache.cxf:cxf-rt-features-logging:${Versions.cxf}")
    implementation("org.apache.cxf:cxf-rt-ws-security:${Versions.cxf}")
    implementation("org.apache.cxf:cxf-rt-ws-policy:${Versions.cxf}")
    implementation("org.apache.cxf:cxf-rt-transports-http:${Versions.cxf}")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:${Versions.cxf}")
    implementation("javax.ws.rs:javax.ws.rs-api:${Versions.javaxWsRsApi}")
    implementation("com.sun.xml.ws:jaxws-ri:${Versions.jaxws}")
    implementation("com.sun.xml.ws:jaxws-tools:${Versions.jaxws}")
    implementation("com.sun.activation:javax.activation:${Versions.javaxActivation}")

    implementation("no.nav.syfotjenester:adresseregisteretv1-tjenestespesifikasjon:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:fastlegeinformasjonv1-tjenestespesifikasjon:${Versions.syfotjenester}")

    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation("net.logstash.logback:logstash-logback-encoder:${Versions.logstashEncoder}")

    // Metrics and Prometheus
    implementation("io.ktor:ktor-metrics-micrometer:${Versions.ktor}")
    implementation("io.micrometer:micrometer-registry-prometheus:${Versions.micrometerRegistry}")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jackson}")
    implementation("javax.xml.bind:jaxb-api:${Versions.jaxb}")
    implementation("org.glassfish.jaxb:jaxb-runtime:${Versions.jaxb}")

    testImplementation("com.nimbusds:nimbus-jose-jwt:${Versions.nimbusJoseJwt}")
    testImplementation("io.ktor:ktor-server-test-host:${Versions.ktor}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("org.amshove.kluent:kluent:${Versions.kluent}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.ktor}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.spek}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.AppKt"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
        mergeServiceFiles()
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
        testLogging.showStandardStreams = true
    }
}
