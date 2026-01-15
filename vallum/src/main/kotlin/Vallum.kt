package com.zenmo.vallum

import com.zenmo.zummon.companysurvey.Project
import com.zenmo.zummon.companysurvey.Survey
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant

data class Vallum
@JvmOverloads
constructor(
    private val clientId: String,
    private val clientSecret: String,
    private val baseUrl: String = "https://ztor.zero.zenmo.com",
    private val tokenUrl: String = "https://keycloak.zenmo.com/realms/zenmo/protocol/openid-connect/token",
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    fun getHessenpoortSurveys(): List<Survey> =
        getSurveysByProject("Hessenpoort")

    @JvmOverloads
    fun getSurveysByProject(project: String, includeInSimulation: Boolean? = true): List<Survey> = runBlocking {
        val accessToken = getAccessToken(client)
        client.get(baseUrl.trimEnd('/') + "/company-surveys") {
            parameter("project", project)
            parameter("includeInSimulation", includeInSimulation)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }.body<List<Survey>>()
    }

    /**
     * Returns the date when the last survey was created, modified, or deleted.
     *
     * This can be used to check if scenarios saved to
     * [Uplux](https://github.com/zenmo/uplux) are outdated.
     * Example:
     *
     * ```java
     * Instant projectLastModifiedAt = vallum.getProjectLastModifiedAt("Wieksteden")
     * for (var scenario : upluxRepository.listScenarios()) {
     *     boolean isOutdated = scenario.getCreatedAt().isBefore(projectLastModifiedAt);
     * }
     * ```
     */
    fun getProjectLastModifiedAt(projectName: String): Instant = runBlocking {
        val accessToken = getAccessToken(client)
        val projects = client.get(baseUrl.trimEnd('/') + "/projects") {
            parameter("name", projectName)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }.body<List<Project>>()

        when (projects.size) {
            0 -> throw ProjectNotFound(projectName)
            1 -> projects[0].lastModifiedAt.toJavaInstant()
            else -> throw IllegalStateException("More than one project with name $projectName found")
        }
    }

    fun getEnabledSurveysByProjectNames(projectNames: Array<String>): List<Survey> = runBlocking {
        val accessToken = getAccessToken(client)
        client.get(baseUrl.trimEnd('/') + "/company-surveys") {
            parameter("projectNames", projectNames.joinToString(","))
            parameter("includeInSimulation", true)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }.body<List<Survey>>()
    }

    /**
     * Get all surveys the account has access to and are configured to include in the simulation
     */
    fun getAllEnabledSurveys(): List<Survey> = runBlocking {
        val accessToken = getAccessToken(client)
        client.get(baseUrl.trimEnd('/') + "/company-surveys") {
            parameter("includeInSimulation", true)
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }.body<List<Survey>>()
    }

    private suspend fun getAccessToken(client: HttpClient): String {
         val response = client.submitForm(
            url = tokenUrl,
            formParameters = parameters {
                append("grant_type", "client_credentials")
                append("client_id", clientId)
                append("client_secret", clientSecret)
            }
        )

        val tokenInfo = response.body<TokenInfo>()
        return tokenInfo.accessToken
    }
}

@Serializable
data class TokenInfo(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
)
