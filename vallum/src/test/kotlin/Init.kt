package com.zenmo.vallum

import com.zenmo.orm.companysurvey.ProjectRepository
import com.zenmo.orm.companysurvey.SurveyRepository
import com.zenmo.orm.companysurvey.createMockSurvey
import com.zenmo.orm.connectToPostgres
import com.zenmo.orm.createSchema
import com.zenmo.orm.getenv
import com.zenmo.orm.user.UserRepository
import com.zenmo.ztor.StopZtor
import com.zenmo.ztor.startTestServer
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object TestProjectNames {
    // user has access to these project
    val WAARDKWARTIER = "Waardkwartier"
    val APPELSCHA = "Appelscha"

    // but not this one
    val HESSENWIEK = "Hessenwiek"
}

@JvmOverloads
fun initZtor(port: Int = 8082): StopZtor {
    val db = connectToPostgres()
    val schema = Schema(db.connector().schema)
    transaction(db) {
        SchemaUtils.dropSchema(schema, cascade = true)
        SchemaUtils.createSchema(schema)
    }
    createSchema(db)

    val projectRepository = ProjectRepository(db)
    val surveyRepository = SurveyRepository(db)

    val accessibleProjectIds = listOf(
        projectRepository.saveNewProject(TestProjectNames.WAARDKWARTIER),
        projectRepository.saveNewProject(TestProjectNames.APPELSCHA),
    )

    projectRepository.saveNewProject(TestProjectNames.HESSENWIEK)
    val userId = UUID.fromString(getenv("USER_ID"))

    // give user access to two of three projects
    UserRepository(db).saveUser(userId, accessibleProjectIds, "Service account test user")

    surveyRepository.save(createMockSurvey(TestProjectNames.WAARDKWARTIER))
    surveyRepository.save(createMockSurvey(TestProjectNames.HESSENWIEK))

    return startTestServer(port)
}


