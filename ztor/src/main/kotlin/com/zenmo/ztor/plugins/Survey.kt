package com.zenmo.ztor.plugins

import com.zenmo.orm.companysurvey.IndexSurveyRepository
import com.zenmo.orm.companysurvey.SurveyRepository
import com.zenmo.orm.deeplink.DeeplinkRepository
import com.zenmo.ztor.deeplink.DeeplinkService
import com.zenmo.ztor.errorMessageToJson
import com.zenmo.ztor.user.getUserId
import com.zenmo.zummon.companysurvey.Survey
import io.ktor.http.*
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.util.UUID
import kotlin.uuid.toKotlinUuid

fun Application.configureSurveys(db: Database): Unit {
    val indexSurveyRepository = IndexSurveyRepository(db)
    val surveyRepository = SurveyRepository(db)
    val deeplinkService = DeeplinkService(DeeplinkRepository(db))

    routing {
        get("/index-surveys") {
            when (val signedInUser = call.getUserId()) {
                null -> call.respond(HttpStatusCode.Unauthorized)
                else -> {
                    val surveys = indexSurveyRepository.getAllSurveys(signedInUser.toKotlinUuid())
                    call.respond(surveys)
                }
            }
        }
        // Create
        post("/company-surveys") {
            val survey: Survey?
            try {
                survey = call.receive<Survey>()
            } catch (e: BadRequestException) {
                if (e.cause is JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.cause?.message))
                    return@post
                }
                call.respond(HttpStatusCode.BadRequest,  errorMessageToJson(e.message))
                return@post
            }

            val surveyId = surveyRepository.save(survey, call.getUserId())

            val deeplink = deeplinkService.generateDeeplink(surveyId)

            call.respond(HttpStatusCode.Created, deeplink)
        }

        // fetch all
        get("/company-surveys") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val repository = SurveyRepository(db)

            val includeInSimulation = call.request.queryParameters["includeInSimulation"]?.toBoolean()
            val project = call.request.queryParameters["project"]
            val projectNames = call.request.queryParameters["projectNames"]?.split(",")

            val surveys = repository.getSurveys(
                userId = userId,
                project = project,
                includeInSimulation = includeInSimulation,
                projectNames = projectNames,
            )

            call.respond(HttpStatusCode.OK, surveys)
        }

        // fetch single
        get("/company-surveys/{surveyId}") {
            val surveyId = UUID.fromString(call.parameters["surveyId"])

            val deeplink = call.request.queryParameters.get("deeplink")
            val secret = call.request.queryParameters.get("secret")

            if (deeplink != null && secret != null) {
                val deeplinkId = UUID.fromString(deeplink)
                deeplinkService.assertValidDeeplink(surveyId, deeplinkId, secret)
                val survey = surveyRepository.getSurveyById(surveyId)
                if (survey == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respond(HttpStatusCode.OK, survey)
            }

            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val survey = surveyRepository.getSurveyByIdWithUserAccessCheck(surveyId, userId)
            if (survey == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(HttpStatusCode.OK, survey)
        }

        delete("/company-surveys/{surveyId}") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val surveyId = UUID.fromString(call.parameters["surveyId"])

            surveyRepository.deleteSurveyById(surveyId, userId)

            call.respond(HttpStatusCode.OK)
        }

        // generate deeplink
        post("/company-surveys/{surveyId}/deeplink") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val surveyId = UUID.fromString(call.parameters["surveyId"])

            val survey = surveyRepository.getSurveyByIdWithUserAccessCheck(surveyId, userId)
            if (survey == null) {
                // User may not have access to this project
                call.respond(HttpStatusCode.NotFound)
                return@post
            }

            val deeplink = deeplinkService.generateDeeplink(surveyId)

            call.respond(HttpStatusCode.Created, deeplink)
        }

        // set active state
        put("/company-surveys/{surveyId}/include-in-simulation") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }

            val surveyId = UUID.fromString(call.parameters["surveyId"])
            val includeInSimulation = call.receive<Boolean>()
            surveyRepository.setIncludeInSimulation(surveyId, userId, includeInSimulation)

            call.respond(HttpStatusCode.OK)
        }
    }
}
