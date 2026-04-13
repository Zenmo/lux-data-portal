package com.zenmo.excelreadnamed.v5

import com.zenmo.zummon.companysurvey.*

fun getSurveyObject(filename: String): Survey {
    var document = CompanyDataDocument.fromFile(filename)
    var companyName = document.getStringField("companyName")

    println("Getting survey data for company: $companyName")

    return document.createSurveyObject()
}
