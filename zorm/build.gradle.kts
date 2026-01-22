import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") // dont think we need this in the ORM
}

group = "com.zenmo.orm"
version = "dev"

repositories {
    maven("https://repo.osgeo.org/repository/release/")
    mavenCentral()
}

val exposed_version = libs.versions.exposed.get()

dependencies {
    implementation(project(":zummon"))
    implementation(project(":joshi"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${libs.versions.kotlinx.serialization.json.get()}")

    implementation("org.postgresql:postgresql:42.7.8")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.3.0")
}

tasks.withType<Test> {
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT,
        )
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}
