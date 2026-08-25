package com.zenmo.ztor.plugins

import com.zenmo.orm.companysurvey.DeleteProjectResult
import com.zenmo.orm.companysurvey.DuplicateProjectNameException
import com.zenmo.orm.companysurvey.ProjectRepository
import com.zenmo.orm.user.UserRepository
import com.zenmo.ztor.errorMessageToJson
import com.zenmo.ztor.user.adminGuardFactory
import com.zenmo.ztor.user.getUserId
import com.zenmo.zummon.companysurvey.Project
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.util.*

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
                call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.message))
                return@post
            }

            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            try {
                val newProject = projectRepository.saveToUser(project, userId)
                call.respond(HttpStatusCode.Created, newProject)
            } catch (e: DuplicateProjectNameException) {
                call.respond(HttpStatusCode.Conflict, errorMessageToJson(e.message))
            }
        }

        // Delete
        delete("/projects/{projectId}") {
            adminGuard(call) {
                val projectId = UUID.fromString(call.parameters["projectId"])
                when (val result = projectRepository.deleteProject(projectId)) {
                    is DeleteProjectResult.Deleted -> call.respond(HttpStatusCode.NoContent)
                    is DeleteProjectResult.NotFound -> call.respond(HttpStatusCode.NotFound, errorMessageToJson(result.message))
                    is DeleteProjectResult.InUse -> call.respond(HttpStatusCode.Conflict, errorMessageToJson(result.message))
                    is DeleteProjectResult.Failure -> {
                        call.application.environment.log.error("Error deleting project", result.cause)
                        call.respond(HttpStatusCode.InternalServerError, errorMessageToJson(result.message))
                    }
                }
            }
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
                call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.message))
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