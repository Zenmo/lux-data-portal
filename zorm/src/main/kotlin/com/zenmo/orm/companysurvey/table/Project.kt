package com.zenmo.orm.companysurvey.table

import com.zenmo.orm.dbutil.ZenmoUUIDTable
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ProjectTable: ZenmoUUIDTable("project") {
    val name = varchar("name", 50).uniqueIndex()
    val energiekeRegioId = integer("energieke_regio_id").uniqueIndex().nullable()

    /**
     * Buurt codes which describe the area of the project.
     * This is sometimes too coarse, we may want to introduce a geometry field.
     */
    val buurtCodes = array<String>("buurt_codes", VarCharColumnType(50)).default(emptyList())

    /**
     * When the project or the associated surveys were last modified.
     */
    val lastModifiedAt = timestamp("last_modified_at").defaultExpression(CurrentTimestamp)
}
