
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
            // exposed >= 0.60.0 seems to cap the constraint name for uinteger
            // at 63 chars which causes a naming conflict
            version("exposed", "0.59.0")
            version("kotlinx-serialization-json", "1.9.0")
            // need to migrate code to kotlin.Instant to update kotlinx-datetime to 0.7.0
            version("kotlinx-datetime", "0.6.2")
            version("kotlinx-rpc", "0.10.1")
            version("ktor", "3.3.3")
        }
    }
}
