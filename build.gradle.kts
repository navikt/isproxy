import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

object Versions {
    const val jacksonVersion = "2.11.3"
    const val jedisVersion = "3.6.0"
    const val ktorVersion = "1.5.4"
    const val jaxbVersion = "2.3.1"
    const val kluentVersion = "1.68"
    const val logbackVersion = "1.2.3"
    const val logstashEncoderVersion = "6.3"
    const val mockkVersion = "1.10.5"
    const val nimbusjosejwtVersion = "7.5.1"
    const val spekVersion = "2.0.15"
    const val micrometerRegistryVersion = "1.7.1"
}

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-auth-jwt:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-client-apache:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-client-jackson:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-jackson:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktorVersion}")

    implementation("ch.qos.logback:logback-classic:${Versions.logbackVersion}")
    implementation("net.logstash.logback:logstash-logback-encoder:${Versions.logstashEncoderVersion}")

    // Metrics and Prometheus
    implementation("io.ktor:ktor-metrics-micrometer:${Versions.ktorVersion}")
    implementation("io.micrometer:micrometer-registry-prometheus:${Versions.micrometerRegistryVersion}")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jacksonVersion}")
    implementation("javax.xml.bind:jaxb-api:${Versions.jaxbVersion}")
    implementation("org.glassfish.jaxb:jaxb-runtime:${Versions.jaxbVersion}")

    testImplementation("com.nimbusds:nimbus-jose-jwt:${Versions.nimbusjosejwtVersion}")
    testImplementation("io.ktor:ktor-server-test-host:${Versions.ktorVersion}")
    testImplementation("io.mockk:mockk:${Versions.mockkVersion}")
    testImplementation("org.amshove.kluent:kluent:${Versions.kluentVersion}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.ktorVersion}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.spekVersion}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spekVersion}") {
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
