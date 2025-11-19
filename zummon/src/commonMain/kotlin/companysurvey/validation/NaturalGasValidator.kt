package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.NaturalGas
import kotlin.math.abs

class NaturalGasValidator : Validator<NaturalGas> {
    override fun validate(naturalGas: NaturalGas): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()

        results.add(validateAnnualElectricityProduction(naturalGas))
        results.add(validateAnnualGasDelivery(naturalGas))

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

    //annual gas delivery should match total of hourly delivery
    fun validateAnnualGasDelivery(naturalGas: NaturalGas): ValidationResult {
        return if (naturalGas.hourlyDelivery_m3?.values == null) {
            ValidationResult(Status.MISSING_DATA, translate("naturalGas.hourlyDeliveryNotProvided"))
        } else {
            val totalHourlyDelivery = naturalGas.hourlyDelivery_m3.values.sum()

            val isCloseEnough = naturalGas.annualDelivery_m3?.toFloat()?.let { annualDelivery ->
                val difference = abs(annualDelivery - totalHourlyDelivery)
                difference <= 0.01f * annualDelivery // 1% tolerance
            } ?: false


            if (isCloseEnough) {
                ValidationResult(Status.VALID, translate("naturalGas.annualGasDeliveryValid", naturalGas.annualDelivery_m3))
            } else {
                ValidationResult(Status.INVALID, translate("naturalGas.annualGasDeliveryMismatch", naturalGas.annualDelivery_m3, totalHourlyDelivery))
            }
        }
    }


}
