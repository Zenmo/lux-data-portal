package com.zenmo.ztor.plugins

import com.zenmo.orm.companysurvey.ProjectRepository
import com.zenmo.orm.user.UserRepository
import com.zenmo.ztor.errorMessageToJson
import com.zenmo.ztor.user.adminGuardFactory
import com.zenmo.ztor.user.getUserId
import com.zenmo.zummon.companysurvey.Project
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import java.util.UUID

fun Application.configureProjects(db: Database): Unit {
    val userRepository = UserRepository(db)
    val projectRepository = ProjectRepository(db)

    val adminGuard = adminGuardFactory(userRepository)

    routing {
        get("/all-projects") {
            adminGuard(call) {
                call.respond(HttpStatusCode.OK, projectRepository.getProjects())
            }
        }

        get("/projects") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            val projectName = call.queryParameters["name"]
            val projects = projectRepository.getProjectsByUserId(userId, projectName)
            call.respond(HttpStatusCode.OK, projects)
        }

        get("/projects/{projectId}") {
            val projectId = UUID.fromString(call.parameters["projectId"])

            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            call.respond(HttpStatusCode.OK, projectRepository.getProjectByUserId(userId, projectId))
        }

        // Create
        post("/projects") {
            val project: Project?
            try {
                project = call.receive<Project>()
            } catch (e: BadRequestException) {
                if (e.cause is JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.cause?.message))
                    return@post
                }
                call.respond(HttpStatusCode.BadRequest,  errorMessageToJson(e.message))
                return@post
            }

            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val newProject = projectRepository.saveToUser(project, userId)

            call.respond(HttpStatusCode.Created, newProject)
        }

        // Update
        put("/projects") {
            val project: Project?
            try {
                project = call.receive<Project>()
            } catch (e: BadRequestException) {
                if (e.cause is JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.cause?.message))
                    return@put
                }
                call.respond(HttpStatusCode.BadRequest,  errorMessageToJson(e.message))
                return@put
            }

            val newProject = projectRepository.save(project)

            call.respond(HttpStatusCode.OK, newProject)
        }

        get("/projects/by-name/{projectName}/buurtcodes") {
            val projectName = call.parameters["projectName"]!!
            call.respond(HttpStatusCode.OK, projectRepository.getBuurtCodesByProjectName(projectName))
        }
    }
}
