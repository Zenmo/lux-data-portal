package com.zenmo.ztor

import com.zenmo.excelreadnamed.v5.CompanyDataDocument
import com.zenmo.orm.companysurvey.ProjectRepository
import com.zenmo.ztor.minio.MinioExcelStorage
import com.zenmo.ztor.user.getUserId
import com.zenmo.zummon.companysurvey.SurveyWithErrors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.*
import org.http4k.core.*
import org.jetbrains.exposed.sql.Database
import kotlin.time.measureTimedValue


@OptIn(InternalAPI::class)
fun Application.configureExcel(db: Database) {
    val projectRepository = ProjectRepository(db)

    routing {
        post("/excel-upload") {
            val userId = call.getUserId()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val maxSize = 20.megabytes

            // This code is very slow (+1 min)
            // I've tried several variantions
            // - with a buffer
            // - forEachPart
            // - directly instantiating io.ktor.http.cio.CIOMultipartDataBase
            // but it doesn't help
//            val multipartData = call.receiveMultipart(maxSize.toLong())
//            val part = multipartData.readPart()
//            if (part !is PartData.FileItem) {
//                call.respond(HttpStatusCode.BadRequest, "Not a file")
//                return@post
//            }
//            val inputStream = part.provider().toInputStream()

            // I experience performance issues with Ktor's multipart handling.
            // This is a workaround using http4k's multipart handling.
            // I want to switch to http4k anyway.
            val multipart = MultipartFormBody.from(
                toHttp4kRequest(call),
                diskThreshold = maxSize,
            )
            val filePart = multipart.file("file")
            if (filePart == null) {
                call.respond(HttpStatusCode.BadRequest, "No file")
                return@post
            }

            val fileBytes = filePart.content.readAllBytes()

            val objectName = MinioExcelStorage.saveExcel(
                inputStream = fileBytes.inputStream(),
                size = fileBytes.size.toLong(),
                userId = userId,
                fileName = filePart.filename,
            )
            log.info("Excel file saved to MinIO as $objectName")

            val (document, excelLoadDuration) = measureTimedValue {
                try {
                    CompanyDataDocument(fileBytes.inputStream(), projectRepository::getProjectByEnergiekeRegioId)
                    // Somehow IOException is not caught by the catch block
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Invalid Excel file: ${e.message}")
                    return@post
                }
            }
            log.info("Time taken to load Excel file: $excelLoadDuration")

            val (survey, excelReadDuration) = measureTimedValue {
                try {
                    document.createSurveyObject()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, buildString {
                        appendLine("Unable to get fields from Excel file:")
                        appendLine(e.toString())
                        appendLine("Trace: ")
                        appendLine(e.stackTraceToString())
                    })
                    return@post
                }
            }
            log.info("Time taken to read values from Excel file: $excelReadDuration")

            call.respond(
                SurveyWithErrors(
                    survey = survey,
                    errors = emptyList(),
                )
            )
        }
    }
}

val Int.megabytes: Int get() = this * 1024 * 1024

suspend fun toHttp4kRequest(call: ApplicationCall): Request {
    val headers = call.request.headers.toMap().map { (k, v) -> k to v.first() }
    val body = MemoryBody(call.receive<ByteArray>())
    return MemoryRequest(Method.POST, Uri.of(call.request.uri), headers, body)
}

