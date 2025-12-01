package com.zenmo.joshi

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Serializable
@JsExport
data class FetchIndexSurveysRequest(
    val projectSearch: String? = null,
    val companySearch: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val order: SurveyOrder? = null,
) {
    /** helpers for JavaScript */
    fun withProjectSearch(projectSearch: String?) = copy(projectSearch = projectSearch)
    fun withCompanySearch(companySearch: String?) = copy(companySearch = companySearch)
    fun withLimit(limit: Int?) = copy(limit = limit)
    fun withOffset(offset: Int?) = copy(offset = offset)
    fun withOrder(order: SurveyOrder?) = copy(order = order)
}
