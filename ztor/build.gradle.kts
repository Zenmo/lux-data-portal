
plugins {
    kotlin("jvm")

    id("io.ktor.plugin") version libs.versions.ktor.get()
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.rpc.plugin")
}

group = "com.zenmo"
version = "0.0.1"

application {
    mainClass.set("com.zenmo.ztor.ApplicationKt")

    // These arguments are only applied when running through Gradle (= in development),
    // not when building and running a Fat Jar (= in production).
    applicationDefaultJvmArgs = listOf(
        "-Dio.ktor.development=true",
        // uncomment to let ztor application listen for debugger
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
        // uncomment to let ztor application connect to debugger on startup
//        "-agentlib:jdwp=transport=dt_socket,server=n,address=172.27.0.1:5005,suspend=y"
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":zorm"))
    implementation(project(":zummon"))
    implementation(project(":joshi"))
    implementation(project(":fudura-client"))
    implementation(project(":excel-read-named-v5"))

    // for file upload
    implementation(platform("com.azure:azure-sdk-bom:1.2.18"))
    implementation("com.azure:azure-storage-blob:12.25.0")

    // Explicitly add Exposed to be able to use the database object for dependency injection and transactions.
    implementation("org.jetbrains.exposed:exposed-core:${libs.versions.exposed.get()}")

    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-auth:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-call-logging-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-core-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-cors-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-host-common-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-html-builder:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-netty-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-status-pages-jvm:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    // kinda need this to run in Azure Container Apps
    // https://learn.microsoft.com/en-us/azure/container-apps/ingress-overview#http-headers
    implementation("io.ktor:ktor-server-forwarded-header:${libs.versions.ktor.get()}")

    // I experience performance issues with Ktor's multipart handling.
    implementation(platform("org.http4k:http4k-bom:5.30.0.0"))
    implementation("org.http4k:http4k-multipart")

    // to call into Keycloak
    implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-client-cio:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-client-content-negotiation:${libs.versions.ktor.get()}")

    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:${libs.versions.kotlinx.rpc.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:${libs.versions.kotlinx.rpc.get()}")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:${libs.versions.kotlinx.rpc.get()}")

    // to hash deeplink secrets
    implementation("at.favre.lib:bcrypt:0.10.2")

    // to decode and validate Keycloak access tokens with user info.
    implementation("com.nimbusds:nimbus-jose-jwt:9.39.2")
    implementation("com.google.crypto.tink:tink:1.13.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.get()}")
    implementation("com.benasher44:uuid:0.8.4")

    // minio for excel uploads storage
    implementation("io.minio:minio:8.5.17")

    testImplementation("io.ktor:ktor-server-test-host:${libs.versions.ktor.get()}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.withType<Test> {
    debugOptions {
        /**
         * If debugging is enabled, Gradle will wait for you
         * to attach a debugger before running the tests.
         *
         * Command to run the ztor-test docker service
         * with a published debug port:
         *
         * docker compose run --publish 127.0.0.1:5005:5005 --rm ztor-test
         */
        //enabled = true
        host = "*"
        suspend = true
    }

    this.testLogging {
        this.showStandardStreams = true
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}
