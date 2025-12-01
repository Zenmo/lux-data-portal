package com.zenmo.joshi

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class SurveyOrder(
    val field: SurveyOrderField,
    val direction: OrderDirection
)

@Serializable
@JsExport
enum class OrderDirection {
    ASC,
    DESC,
}

@Serializable
@JsExport
enum class SurveyOrderField {
    CREATION_DATE,
    PROJECT_NAME,
    COMPANY_NAME,
    INCLUDE_IN_SIMULATION,
}
