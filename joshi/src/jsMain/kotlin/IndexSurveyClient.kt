package com.zenmo.joshi

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.rpc.withService
import kotlin.js.Promise

/**
 * Wraps IndexSurveyService because suspend functions can't be exported to JavaScript.
 */
@JsExport
class IndexSurveyClient(
    val wrappedService: IndexSurveyService = rpcClient.withService(IndexSurveyService::class)
) {
    fun fetchIndexSurveys(): Promise<IndexSurveyList> {
        return GlobalScope.promise {
            wrappedService.fetchIndexSurveys()
        }
    }
}
