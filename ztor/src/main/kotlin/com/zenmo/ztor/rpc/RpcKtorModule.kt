package com.zenmo.ztor.rpc

import com.zenmo.joshi.IndexSurveyService
import com.zenmo.orm.companysurvey.SurveyRepository
import com.zenmo.ztor.companysurvey.IndexSurveyServiceImpl
import com.zenmo.ztor.user.getUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.jetbrains.exposed.sql.Database

fun Application.configureRpc(db: Database) {
    val surveyRepository = SurveyRepository(db)

    install(Krpc) {
        serialization {
            json()
        }
    }

    routing {
        rpc("/rpc") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@rpc
            }

            registerService<IndexSurveyService> {
                IndexSurveyServiceImpl(surveyRepository, userId)
            }
        }
    }
}
