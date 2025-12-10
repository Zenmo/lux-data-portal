import java.net.URI

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.rpc.plugin")
}

group = "com.zenmo"
version = System.getenv("VERSION_TAG") ?: "dev"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
    }
    js(IR) {
        useEsModules()
        generateTypeScriptDefinitions()
        binaries.library()
        browser {
        }
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-rpc-core:${libs.versions.kotlinx.rpc.get()}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${libs.versions.kotlinx.serialization.json.get()}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${libs.versions.kotlinx.serialization.json.get()}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.get()}")
                implementation(project(":zummon"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jsMain {
            dependencies {
                // align versions with frontend
                implementation(npm("@js-joda/core", "^5.6.3"))
                implementation(npm("@js-joda/timezone", "^2.21.1"))

                implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:${libs.versions.kotlinx.rpc.get()}")
                implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:${libs.versions.kotlinx.rpc.get()}")
                implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:${libs.versions.kotlinx.rpc.get()}")

                implementation("io.ktor:ktor-client-core:${libs.versions.ktor.get()}")
                implementation("io.ktor:ktor-client-js:${libs.versions.ktor.get()}")
            }
        }
    }
}
