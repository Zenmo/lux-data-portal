package com.zenmo.ztor.companysurvey

import com.zenmo.joshi.FetchIndexSurveysRequest
import com.zenmo.orm.cleanDb
import com.zenmo.orm.companysurvey.ProjectRepository
import com.zenmo.orm.companysurvey.SurveyRepository
import com.zenmo.orm.companysurvey.createMockSurvey
import com.zenmo.orm.connectToPostgres
import com.zenmo.orm.user.UserRepository
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.measureTime

/**
 * Benchmark how long it takes to load and validate a large number of surveys.
 * This was to validate that pagination is necessary.
 * 500 surveys takes about 6.5 seconds on my laptop.
 */
class IndexSurveyBenchmark {
    @Test
    @Ignore
    fun benchmarkMassSurveyValidation() {
        val n = 500
        val db = connectToPostgres()
        cleanDb(db)

        val projectName = "MyProject"
        val userId = UUID.randomUUID()

        val surveyRepository = SurveyRepository(db)

        val projectId = ProjectRepository(db).saveNewProject(projectName)
        UserRepository(db).saveUser(userId, listOf(projectId))

        repeat(n) {
            surveyRepository.save(
                createMockSurvey(projectName, 365.days)
            )
        }

        val indexSurveyService = IndexSurveyServiceImpl(surveyRepository, userId)
        val timeTaken = measureTime {
            val indexSurveyResponse = runBlocking {
                indexSurveyService.fetchIndexSurveys(FetchIndexSurveysRequest())
            }

            assertEquals(n, indexSurveyResponse.records.size)
        }

        println("Took $timeTaken")
    }
}
