package com.zenmo.orm.companysurvey

import com.zenmo.joshi.IndexSurvey
import com.zenmo.orm.companysurvey.table.CompanySurveyTable
import com.zenmo.orm.companysurvey.table.ProjectTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class IndexSurveyRepository(
    val db: Database
) {
    fun getAllSurveys(
        userId: Uuid,
    ): List<IndexSurvey> {
        return transaction(db) {
            CompanySurveyTable
                .join(ProjectTable, JoinType.INNER, CompanySurveyTable.projectId, ProjectTable.id)
                .selectAll()
                .where {
                    userIsAllowedCondition(userId.toJavaUuid())
                }
                .map {
                    IndexSurvey(
                        id = it[CompanySurveyTable.id].toKotlinUuid(),
                        companyName = it[CompanySurveyTable.companyName],
                        projectName = it[ProjectTable.name],
                        creationDate = it[CompanySurveyTable.created],
                        includeInSimulation = it[CompanySurveyTable.includeInSimulation]
                    )
                }
        }
    }
}
