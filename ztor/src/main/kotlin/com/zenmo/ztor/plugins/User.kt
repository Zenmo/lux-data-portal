package com.zenmo.ztor.plugins

import com.zenmo.orm.user.UserRepository
import com.zenmo.ztor.errorMessageToJson
import com.zenmo.ztor.user.adminGuardFactory
import com.zenmo.zummon.User

import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import java.util.*

fun Application.configureUserEndpoints(db: Database): Database {
    val userRepository = UserRepository(db)

    val adminGuard = adminGuardFactory(userRepository)

    routing {
        get("/users") {
            adminGuard(call) {
                val users = userRepository.getUsersAndProjects()
                call.respond(HttpStatusCode.OK, users)
            }
        }

        get("/users/{userId}") {
            adminGuard(call) {
                val userId = UUID.fromString(call.parameters["userId"])
                val user = userRepository.getUserById(userId)
                call.respond(HttpStatusCode.OK, user)
            }
        }

        get("/users/{userId}/projects") {
            adminGuard(call) {
                val userId = UUID.fromString(call.parameters["userId"])
                val user = userRepository.getUserAndProjects(userId)
                call.respond(HttpStatusCode.OK, user)
            }
        }

        // Create
        post("/users") {
            val user: User?
            try {
                user = call.receive<User>()
            } catch (e: BadRequestException) {
                if (e.cause is JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.cause?.message))
                    return@post
                }
                call.respond(HttpStatusCode.BadRequest,  errorMessageToJson(e.message))
                return@post
            }
            adminGuard(call) {
                val newUser = userRepository.save(user)
                call.respond(HttpStatusCode.Created, newUser)
            }
        }

        // Update
        put("/users") {
            val user: User?
            try {
                user = call.receive<User>()
            } catch (e: BadRequestException) {
                if (e.cause is JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest, errorMessageToJson(e.cause?.message))
                    return@put
                }
                call.respond(HttpStatusCode.BadRequest,  errorMessageToJson(e.message))
                return@put
            }
            adminGuard(call) {
                val newUser = userRepository.save(user)
                call.respond(HttpStatusCode.OK, newUser)
            }
        }

        delete("/users/{userId}") {
            adminGuard(call) {
                val userId = UUID.fromString(call.parameters["userId"])
                userRepository.deleteUserById(userId)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
    return db
}
