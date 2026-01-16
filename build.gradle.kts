plugins {
    // need to specify version here because this plugin in used in multiple subprojects
    kotlin("jvm") version "2.3.0" apply false
    kotlin("plugin.serialization") version "2.3.0" apply false
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.1" apply false
}
