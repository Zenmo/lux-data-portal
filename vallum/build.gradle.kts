import java.net.URI
import org.gradle.api.component.AdhocComponentWithVariants
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    // Shadow is used for backwards compatibility testing
    id("com.gradleup.shadow") version "9.2.2"
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.dorongold.task-tree") version "4.0.2"
}
group = "com.zenmo"
version = System.getenv("VERSION_TAG") ?: "dev"

repositories {
    maven("https://repo.osgeo.org/repository/release/")
    mavenCentral()
}

val ktor_version = "3.5.2"

dependencies {
    testImplementation(kotlin("test"))
    // Ztor is started in the test.
    testImplementation(project(":ztor"))
    testImplementation(project(":zorm"))
    testImplementation(project(":zummon"))
    testImplementation("org.jetbrains.exposed:exposed-core:${libs.versions.exposed.get()}")

    // Zummon is bundled into the jar as a part of Vallum.
    // `compileOnly` keeps it out of the published POM and module metadata.
    compileOnly(project(":zummon"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${libs.versions.kotlinx.serialization.json.get()}")
    api("org.jetbrains.kotlinx:kotlinx-datetime:${libs.versions.kotlinx.datetime.get()}")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}

tasks.test {
    useJUnitPlatform()
    /**
     * To debug tests:
     *
     * - check your docker host ip
     * - uncomment the line below
     * - listen for debug connections on port 5005
     * - run `docker compose run --rm vallum-test`
     */
    //jvmArgs("-agentlib:jdwp=transport=dt_socket,server=n,address=172.27.0.1:5005,suspend=y")
}

java {
    withSourcesJar()
}

// Add zummon to artifact
tasks.jar {
    from(project(":zummon").the<SourceSetContainer>()["jvmMain"].output)
}

// Add zummon Kotlin source files to artifact
tasks.named<Jar>("sourcesJar") {
    from(project(":zummon").the<KotlinMultiplatformExtension>().sourceSets["commonMain"].kotlin)
}

// The shadow plugin adds the fat jar for publishing as shadowRuntimeElements.
// This keeps it out of the publication.
afterEvaluate {
    (components["java"] as AdhocComponentWithVariants).withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
        skip()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.zenmo"
            artifactId = "vallum"
            version = System.getenv("VERSION_TAG") ?: "dev"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/zenmo/lux-data-portal")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
