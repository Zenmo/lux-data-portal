package com.zenmo.orm

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Drop and recreate the database schema.
 * To use in tests.
 */
fun cleanDb(db: Database) {
    val schema = Schema(db.connector().schema)
    transaction(db) {
        SchemaUtils.dropSchema(schema, cascade = true)
        SchemaUtils.createSchema(schema)
    }
    createSchema(db)
}
