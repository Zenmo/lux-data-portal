package com.zenmo.joshi

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class FetchIndexSurveysResponse(
    val records: List<IndexSurvey>,
    /**
     * Total number of Survey records matching the query.
     * Can be more than [records.size] if a limit or offset was used.
     */
    val totalCount: Int,
)
