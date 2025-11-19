package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.Address
import com.zenmo.zummon.companysurvey.Survey
import kotlin.js.JsExport

@JsExport
fun interface Validator<T> {
    fun validate(item: T): List<ValidationResult>
}

@JsExport
data class ValidationResult(
    val status: Status,
    val message: String,
)

@JsExport
enum class Status {
    VALID,
    INVALID,
    MISSING_DATA,
    NOT_APPLICABLE,
}

@JsExport
val surveyValidator = Validator<Survey> { survey: Survey ->
    survey.addresses.flatMap {
        addressValidator.validate(it)
    }
}

val addressValidator = Validator<Address> { address: Address ->
    address.gridConnections.flatMap {
        GridConnectionValidator().validate(it)
    }
}
