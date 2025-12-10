package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.NaturalGas
import kotlin.math.abs

class NaturalGasValidator : Validator<NaturalGas> {
    override fun validate(naturalGas: NaturalGas): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()

        results.add(validateAnnualElectricityProduction(naturalGas))
        results.addAll(validateAnnualGasDelivery(naturalGas))

        return results
    }

    //hasNaturalGasConnection true -> should have annual delivery
    fun validateAnnualElectricityProduction(naturalGas: NaturalGas): ValidationResult {
        return when {
            naturalGas.hasConnection == true && (naturalGas.annualDelivery_m3 ?: 0) == 0 ->
                ValidationResult(Status.MISSING_DATA, translate("naturalGas.annualDeliveryNotProvided"))
            else ->
                ValidationResult(Status.NOT_APPLICABLE, translate("naturalGas.withoutConnection"))
        }
    }

    // annual gas delivery should match total of hourly delivery
    fun validateAnnualGasDelivery(naturalGas: NaturalGas): List<ValidationResult> {
        if (naturalGas.hourlyDelivery_m3 == null) {
            return listOf(
                ValidationResult(
                    Status.MISSING_DATA,
                    translate("naturalGas.hourlyDeliveryNotProvided")
                )
            )
        }

        val timeSeriesValidationResult = validateTimeSeries(naturalGas.hourlyDelivery_m3)

        val totalHourlyDelivery = naturalGas.hourlyDelivery_m3.values.sum()

        if (naturalGas.annualDelivery_m3 == null) return listOf(
            timeSeriesValidationResult,
            ValidationResult(Status.MISSING_DATA, message(
                en = "Annual gas delivery not provided",
                nl = "Jaarlevering gas niet opgegeven",
            ))
        )

        val isCloseEnough = naturalGas.annualDelivery_m3.toFloat().let { annualDelivery ->
            val difference = abs(annualDelivery - totalHourlyDelivery)
            difference <= 0.01f * annualDelivery // 1% tolerance
        }

        return listOf(
            timeSeriesValidationResult,
            if (isCloseEnough) {
                ValidationResult(Status.VALID, translate("naturalGas.annualGasDeliveryValid", naturalGas.annualDelivery_m3))
            } else {
                ValidationResult(Status.INVALID, translate("naturalGas.annualGasDeliveryMismatch", naturalGas.annualDelivery_m3, totalHourlyDelivery))
            }
        )
    }
}
