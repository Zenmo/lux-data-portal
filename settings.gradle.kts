
rootProject.name = "zeroweb"
include(
    "zorm",
    "ztor",
    "joshi",
    "zummon",
    "vallum",
    "excel-read-named-v5",
    "fudura-client"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("exposed", "0.59.0")
            version("kotlinx-serialization-json", "1.8.0")
            version("kotlinx-datetime", "0.6.1")
            version("kotlinx-rpc", "0.10.1")
            version("ktor", "3.3.3")
        }
    }
}
