package com.zenmo.zummon.companysurvey

import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.uuid.Uuid

@JsExport
@Serializable
data class Address(
    val id: Uuid = Uuid.generateV7(),

    val street: String,
    val houseNumber: Int,
    val houseLetter: String = "", // A-Z allowed
    val houseNumberSuffix: String = "",
    val postalCode: String = "",
    val city: String,

    val gridConnections: List<GridConnection> = emptyList(),
) {
    /**
     * For JavaScript
     */
    public val gridConnectionArray: Array<GridConnection>
        get() = gridConnections.toTypedArray()

    fun clearIds() = copy(
        id = Uuid.generateV7(),
        gridConnections = gridConnections.map { it.clearId() }
    )
}
