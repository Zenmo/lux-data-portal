package com.zenmo.ztor.rpc

import com.zenmo.joshi.IndexSurveyList
import com.zenmo.joshi.IndexSurveyService
import com.zenmo.orm.companysurvey.SurveyRepository

class IndexSurveyServiceImpl(
    val surveyRepository: SurveyRepository
): IndexSurveyService {
    override suspend fun fetchIndexSurveys(): IndexSurveyList {
        return IndexSurveyList(emptyList(), 0)
    }
}
