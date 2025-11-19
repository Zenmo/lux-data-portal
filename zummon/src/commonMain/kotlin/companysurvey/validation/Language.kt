package com.zenmo.zummon.companysurvey.validation

import kotlin.js.JsExport

@JsExport
fun setLanguage(language: Language) {
    currentLanguage = language
}

fun getLanguage(): Language {
    return currentLanguage
}

@JsExport
enum class Language {
    en,
    nl
}

private
var currentLanguage: Language = Language.en // Set default language
