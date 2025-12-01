package com.zenmo.ztor.rpc

import com.zenmo.joshi.IndexSurveyService
import com.zenmo.orm.companysurvey.SurveyRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.jetbrains.exposed.sql.Database

fun Application.configureRpc(db: Database) {
    install(Krpc) {
        serialization {
            json()
        }
    }

    routing {
        rpc("/rpc") {
            registerService<IndexSurveyService> {
                IndexSurveyServiceImpl(SurveyRepository(db))
            }
        }
    }
}
