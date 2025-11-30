package com.zenmo.joshi

import com.zenmo.zummon.companysurvey.Survey
import com.zenmo.zummon.jsonDecoder
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.uuid.Uuid

/**
 * A variant of the company survey which has less properties
 * so it can be used for a list on a web page.
 */
@JsExport
@Serializable
data class IndexSurvey(
    val id: Uuid,
    val companyName: String,
    val projectName: String,
    val creationDate: Instant,
    val includeInSimulation: Boolean
) {
    public fun withIncludeInSimulation(includeInSimulation: Boolean): IndexSurvey {
        return this.copy(includeInSimulation = includeInSimulation)
    }
}


@JsExport
fun indexSurveysFromJson(json: String): Array<IndexSurvey> {
    return jsonDecoder.decodeFromString<Array<IndexSurvey>>(json)
}
