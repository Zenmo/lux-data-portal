package com.zenmo.ztor.user

import com.zenmo.orm.user.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

/**
 * Only execute the provided function body if the user is an admin.
 */
fun adminGuardFactory(userRepository: UserRepository): suspend (RoutingCall, suspend () -> Unit) -> Unit {
    return middleware@ { call: RoutingCall, body: suspend() -> Unit ->
        val userId = call.getUserId()

        if (userId == null) {
            call.respond(HttpStatusCode.Companion.Unauthorized, "User not logged in")
            return@middleware
        }

        if (!userRepository.isAdmin(userId)) {
            call.respond(HttpStatusCode.Companion.Forbidden, "User is not admin")
            return@middleware
        }

        body()
    }
}
