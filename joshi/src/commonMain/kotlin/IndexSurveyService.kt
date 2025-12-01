package com.zenmo.joshi

import kotlinx.rpc.annotations.Rpc

@Rpc
interface IndexSurveyService {
    suspend fun fetchIndexSurveys(): IndexSurveyList
}
