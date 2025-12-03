package com.zenmo.ztor.companysurvey

import com.zenmo.joshi.FetchIndexSurveysRequest
import com.zenmo.joshi.FetchIndexSurveysResponse
import com.zenmo.joshi.IndexSurvey
import com.zenmo.joshi.IndexSurveyService
import com.zenmo.orm.companysurvey.SurveyFilters
import com.zenmo.orm.companysurvey.SurveyQueryParameters
import com.zenmo.orm.companysurvey.SurveyRepository
import com.zenmo.zummon.companysurvey.Survey
import com.zenmo.zummon.companysurvey.validation.Status
import com.zenmo.zummon.companysurvey.validation.surveyValidator
import java.util.UUID
import kotlin.uuid.toKotlinUuid

class IndexSurveyServiceImpl(
    val surveyRepository: SurveyRepository,
    val userId: UUID,
): IndexSurveyService {
    override suspend fun fetchIndexSurveys(request: FetchIndexSurveysRequest): FetchIndexSurveysResponse {
        val queryResult = surveyRepository.getSurveysWithTotalCount(
            SurveyQueryParameters(
                filters = SurveyFilters(
                    userId = userId,
                    companySearch = request.companySearch,
                    projectSearch = request.projectSearch,
                ),
                limit = request.limit,
                offset = request.offset,
                order = request.order,
            )
        )
        val indexSurveys = queryResult.surveys.map(::validateSurvey)

        return FetchIndexSurveysResponse(indexSurveys, queryResult.totalCount)
    }

    private fun validateSurvey(survey: Survey): IndexSurvey {
        val failedValidationMessages = surveyValidator
            .validate(survey)
            .filter { it.status == Status.INVALID }
            .map { it.message }

        return IndexSurvey(
            id = survey.id.toKotlinUuid(),
            companyName = survey.companyName,
            projectName = survey.zenmoProject,
            creationDate = survey.createdAt,
            includeInSimulation = survey.includeInSimulation,
            failedValidationMessages = failedValidationMessages
        )
    }
}
