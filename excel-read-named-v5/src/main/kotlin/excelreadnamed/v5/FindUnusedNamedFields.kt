package com.zenmo.excelreadnamed.v5

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    findUnusedNamedFields()
}

/**
 * Finds and logs fields in the Excel document
 * which are not used in this codebase.
 *
 * This is to check if we implemented all the fields.
 */
fun findUnusedNamedFields() {
    val workbook = XSSFWorkbook("/home/erik/tmp/energieke-regio.xlsx")
    val fieldsInExcel = workbook.allNames.map { it.nameName }

    val sourceFile = "src/main/kotlin/excelreadnamed/v5/CompanyDataDocument.kt"
    val sourceFileContent = Files.readString(Paths.get(sourceFile))

    val fieldsNotFound = fieldsInExcel.filter { "\"$it\"" !in sourceFileContent }

    println("Fields found in Excel but not in source code:")
    fieldsNotFound.forEach { println(it) }
}
