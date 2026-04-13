package com.zenmo.orm.companysurvey

import com.zenmo.orm.blob.BlobPurpose
import com.zenmo.orm.companysurvey.table.AddressTable
import com.zenmo.orm.companysurvey.table.FileTable
import com.zenmo.orm.companysurvey.table.GridConnectionTable
import com.zenmo.zummon.companysurvey.File
import com.zenmo.zummon.companysurvey.GridConnection
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class FileRepository(
    private val db: Database
) {
    fun getFileByBlobName(blobName: String): File? = transaction(db) {
        FileTable.selectAll().where { FileTable.blobName eq blobName }.firstOrNull()?.let {
            hydrateFile(it)
        }
    }

    fun hydrateFile(row: ResultRow): File {
        return File(
            blobName = row[FileTable.blobName],
            originalName = row[FileTable.originalName],
            size = row[FileTable.size],
            contentType = row[FileTable.contentType],
        )
    }

    /**
     * Delete files associated with a survey and return their blob names.
     * Used when deleting a survey.
     */
    fun deleteBySurveyId(surveyId: UUID): List<String> {
        return FileTable.deleteReturning(listOf(FileTable.blobName)) {
            FileTable.gridConnectionId eq anyFrom(
                GridConnectionTable.select(GridConnectionTable.id).where(
                    GridConnectionTable.addressId eq anyFrom(
                        AddressTable.select(AddressTable.id)
                            .where(AddressTable.surveyId eq surveyId)
                    )
                )
            )
        }.map { it[FileTable.blobName] }
    }

    /**
     * Fetch files for given grid connection IDs, grouped by purpose and grid connection ID.
     */
    fun getFilesGroupedByPurposeAndGridConnectionId(gridConnectionIds: List<UUID>): Map<Pair<BlobPurpose, Uuid>, List<File>> {
        return FileTable.selectAll()
            .where {
                FileTable.gridConnectionId inList gridConnectionIds
            }
            .toList()
            .groupBy { Pair(it[FileTable.purpose], it[FileTable.gridConnectionId].toKotlinUuid()) }
            .mapValues {
                it.value.map { hydrateFile(it) }
            }
    }

    /**
     * Upsert files for given grid connections.
     *
     * This does not delete files that are removed from the grid connections.
     * This is not a priority to fix because this "Files" feature is not used at the moment.
     */
    fun upsertFiles(gridConnections: List<GridConnection>) {
        for (gridConnection in gridConnections) {
            for (electricityFile in gridConnection.electricity.quarterHourlyValuesFiles) {
                FileTable.upsert {
                    it[gridConnectionId] = gridConnection.id.toJavaUuid()
                    it[purpose] = BlobPurpose.ELECTRICITY_VALUES
                    it[blobName] = electricityFile.blobName
                    it[originalName] = electricityFile.originalName
                    it[size] = electricityFile.size
                    it[contentType] = electricityFile.contentType
                }
            }

            val authorizationFile = gridConnection.electricity.authorizationFile
            if (authorizationFile != null) {
                FileTable.upsert {
                    it[gridConnectionId] = gridConnection.id.toJavaUuid()
                    it[purpose] = BlobPurpose.ELECTRICITY_AUTHORIZATION
                    it[blobName] = authorizationFile.blobName
                    it[originalName] = authorizationFile.originalName
                    it[size] = authorizationFile.size
                    it[contentType] = authorizationFile.contentType
                }
            }

            for (gasFile in gridConnection.naturalGas.hourlyValuesFiles) {
                FileTable.upsert {
                    it[gridConnectionId] = gridConnection.id.toJavaUuid()
                    it[purpose] = BlobPurpose.NATURAL_GAS_VALUES
                    it[blobName] = gasFile.blobName
                    it[originalName] = gasFile.originalName
                    it[size] = gasFile.size
                    it[contentType] = gasFile.contentType
                }
            }
        }
    }
}
