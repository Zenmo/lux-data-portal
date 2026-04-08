plugins {
    // need to specify version here because this plugin in used in multiple subprojects
    kotlin("jvm") version "2.3.20" apply false
    kotlin("plugin.serialization") version "2.3.20" apply false
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.2" apply false
}
