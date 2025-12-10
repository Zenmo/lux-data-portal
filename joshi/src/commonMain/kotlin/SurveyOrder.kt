package com.zenmo.joshi

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic

@Serializable
@JsExport
data class SurveyOrder(
    val field: SurveyOrderField,
    val direction: OrderDirection
)

@Serializable
@JsExport
enum class OrderDirection(
    val int: Int
) {
    ASC(1),
    DESC(-1);

    companion object {
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun fromInt(int: Int): OrderDirection = when (int) {
            1 -> ASC
            -1 -> DESC
            else -> throw IllegalArgumentException("Unknown order direction: $int")
        }
    }
}

@Serializable
@JsExport
enum class SurveyOrderField(
    val fieldName: String,
) {
    CREATION_DATE("creationDate"),
    PROJECT_NAME("projectName"),
    COMPANY_NAME("companyName"),
    INCLUDE_IN_SIMULATION("includeInSimulation");

    companion object {
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun fromFieldName(fieldName: String): SurveyOrderField =
            entries.singleOrNull {
                it.fieldName == fieldName
            } ?: throw IllegalArgumentException("Unknown order field name: $fieldName")
    }
}
