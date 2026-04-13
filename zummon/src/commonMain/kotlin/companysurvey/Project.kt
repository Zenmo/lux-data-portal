package com.zenmo.zummon.companysurvey

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

@Serializable
@JsExport
data class Project(
    val id: Uuid = Uuid.generateV7(),
    val name: String = "",
    // Project ID aka Energy Hub ID of Energieke Regio.
    val energiekeRegioId: Int? = null,
    val buurtCodes: List<String> = emptyList(),
    val lastModifiedAt: Instant = Clock.System.now()
)

@JsExport
fun projectsFromJson(json: String): Array<Project> {
    return kotlinx.serialization.json.Json.decodeFromString<Array<Project>>(json)
}

@JsExport
fun projectFromJson(json: String): Project {
    return kotlinx.serialization.json.Json.decodeFromString<Project>(json)
}
