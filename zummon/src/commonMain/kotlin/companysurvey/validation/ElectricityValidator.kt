package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.CompanyGrootverbruik
import com.zenmo.zummon.companysurvey.Electricity
import com.zenmo.zummon.companysurvey.KleinverbruikElectricityConnectionCapacity
import com.zenmo.zummon.companysurvey.KleinverbruikOrGrootverbruik
import kotlin.math.abs

class ElectricityValidator : Validator<Electricity> {
    override fun validate(electricity: Electricity): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()

        results.addAll(validateKleinOrGroot(electricity))
        results.add(validateContractedCapacity(electricity))
        results.add(validateAnnualProductionFeedIn(electricity))
        results.add(validateContractedFeedInCapacity(electricity))

        results.add(validateAnnualFeedInMatchesQuarterHourlyFeedIn(electricity))
        results.add(validateQuarterHourlyDeliveryData(electricity))
        results.add(validateQuarterHourlyProductionData(electricity))
        results.add(quarterHourlyDeliveryLowContractedCapacity(electricity))
        results.add(quarterFeedInLowContractedCapacity(electricity))
        return results
    }

    fun validateKleinOrGroot(electricity: Electricity): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()
        val kleinOrGroot = electricity.kleinverbruikOrGrootverbruik

        if (kleinOrGroot == null) {
            results.add(
                ValidationResult(
                    Status.MISSING_DATA,
                    translate("electricity.kleinverbruikOrGrootverbruikNoDefined")
                )
            )
        } else {
            // Validate grootverbruik data
            if (kleinOrGroot == KleinverbruikOrGrootverbruik.GROOTVERBRUIK) {
                results.addAll(validateGrootverbruik(electricity.grootverbruik))
                results.add(validateGrootverbruikPhysicalCapacity(electricity))
            }

            // Validate kleinverbruik data
            if (kleinOrGroot == KleinverbruikOrGrootverbruik.KLEINVERBRUIK) {
                results.add(validateKleinverbruik(electricity))
            }
        }

        return results
    }

    fun validateGrootverbruik(grootverbruik: CompanyGrootverbruik?): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()

        if (grootverbruik == null) {
            results.add(ValidationResult(Status.INVALID, translate("grootverbruik.notProvided")))
        } else {
            grootverbruik.contractedConnectionDeliveryCapacity_kW ?: results.add(ValidationResult(Status.MISSING_DATA, translate("grootverbruik.connectionCapacityNotProvide")))
            grootverbruik.physicalCapacityKw ?: results.add(ValidationResult(Status.MISSING_DATA, translate("grootverbruik.physicalCapacityNotProvide")))
            grootverbruik.contractedConnectionFeedInCapacity_kW ?: results.add(ValidationResult(Status.MISSING_DATA, translate("grootverbruik.connectionFeedInCapacityNotProvide")))
        }

        return results
    }

    // Validator for grootverbruik physical connection > 3x80A (55.2 kW)
    fun validateGrootverbruikPhysicalCapacity(electricity: Electricity): ValidationResult {
        val connectionCapacity = electricity.getPhysicalConnectionCapacityKw()

        return if (connectionCapacity == null) {
            ValidationResult(Status.MISSING_DATA, translate("grootverbruik.physicalCapacityNotProvide")) // Same validation in multiple places
        } else if (connectionCapacity > KleinverbruikElectricityConnectionCapacity._3x80A.toKw()) {
            ValidationResult(Status.VALID, translate("grootverbruik.valid"))
        } else {
            ValidationResult(Status.INVALID, translate("grootverbruik.invalid", connectionCapacity))
        }
    }

    fun validateKleinverbruik(electricity: Electricity): ValidationResult {
        // Check if kleinverbruikOrGrootverbruik is KLEINVERBRUIK
        return if (electricity.kleinverbruikOrGrootverbruik == KleinverbruikOrGrootverbruik.KLEINVERBRUIK) {
            val kleinverbruik = electricity.kleinverbruik

            // If kleinverbruik is null, return invalid
            if (kleinverbruik == null) {
                ValidationResult(Status.MISSING_DATA, translate("kleinverbruik.notProvided"))
            }
            // If kleinverbruik.connectionCapacity is not null, perform the validation
            else if (kleinverbruik.connectionCapacity != null) {
                // Compare kleinverbruik connection capacity with 3x80A using enum comparison
                if (kleinverbruik.connectionCapacity <= KleinverbruikElectricityConnectionCapacity._3x80A) {
                    ValidationResult(Status.VALID, translate("kleinverbruik.valid"))
                } else {
                    ValidationResult(
                        Status.INVALID,
                        translate("kleinverbruik.exceedsLimit", kleinverbruik.connectionCapacity)
                    )
                }
            }
            // If connection capacity is null, return invalid
            else {
                ValidationResult(Status.INVALID, translate("kleinverbruik.invalid"))
            }
        }
        // If kleinverbruikOrGrootverbruik is not KLEINVERBRUIK, return not applicable
        else {
            ValidationResult(Status.NOT_APPLICABLE, translate("kleinverbruik.notApplicable"))
        }
    }

    // Validator for contracted delivery capacity <= physical capacity
    fun validateContractedCapacity(electricity: Electricity): ValidationResult {
        val contractedCapacity = electricity.getContractedConnectionCapacityKw()
        val physicalCapacity = electricity.getPhysicalConnectionCapacityKw()

        return when {
            contractedCapacity == null -> ValidationResult(Status.MISSING_DATA, translate("electricity.contractedCapacityNotProvided"))
            physicalCapacity == null -> ValidationResult(Status.MISSING_DATA, translate("electricity.physicalCapacityNotProvide"))
            contractedCapacity <= physicalCapacity -> ValidationResult(Status.VALID, translate("electricity.contractedDeliveryCapacityValid", contractedCapacity))
            else -> ValidationResult(Status.INVALID, translate("electricity.contractedDeliveryCapacityExceeds", contractedCapacity, physicalCapacity))
        }
    }

    // Validator for contracted feed-in capacity <= physical capacity
    fun validateContractedFeedInCapacity(electricity: Electricity): ValidationResult {
        val feedInCapacity = electricity.getContractedFeedInCapacityKw()
        val physicalCapacity = electricity.getPhysicalConnectionCapacityKw()

        return when {
            feedInCapacity == null -> ValidationResult(Status.MISSING_DATA, translate("electricity.connectionFeedInCapacityNotProvide"))
            physicalCapacity == null -> ValidationResult(Status.MISSING_DATA, translate("electricity.physicalCapacityNotProvide"))
            feedInCapacity <= physicalCapacity -> ValidationResult(Status.VALID, translate("electricity.feedInLowerPhysicalCapacity", feedInCapacity, physicalCapacity))
            else -> ValidationResult(Status.INVALID, translate("electricity.feedInExceedPhysicalCapacity", feedInCapacity, physicalCapacity))
        }
    }

    // Annual pv production should be more than annual feed-in
    fun validateAnnualProductionFeedIn(electricity: Electricity): ValidationResult {
        val annualProduction = electricity.annualElectricityProduction_kWh
        val feedIn = electricity.annualElectricityFeedIn_kWh

        return when {
            annualProduction == null -> ValidationResult(Status.MISSING_DATA, translate("electricity.annualElectricityProductionNotProvided"))
            feedIn == null -> ValidationResult(Status.MISSING_DATA, translate("electricity.annualElectricityFeedInNotProvided"))
            annualProduction >= feedIn -> ValidationResult(Status.VALID, translate("electricity.annualProductionFeedInValid", annualProduction, feedIn))
            else -> ValidationResult(Status.INVALID, translate("electricity.annualProductionFeedInInvalid", annualProduction, feedIn))
        }
    }

    //annual feed-in should match total of quarter-hourly feed-in
    fun validateAnnualFeedInMatchesQuarterHourlyFeedIn(electricity: Electricity): ValidationResult {
        if (electricity.quarterHourlyFeedIn_kWh == null) {
            return ValidationResult(Status.MISSING_DATA, translate("electricity.quarterHourlyFeedInNotProvided"))
        }

        return if (electricity.quarterHourlyFeedIn_kWh.hasFullYear() == true) {
            val totalQuarterHourlyFeedIn = electricity.quarterHourlyFeedIn_kWh.values.sum()

            val isCloseEnough = electricity.annualElectricityFeedIn_kWh?.toFloat()?.let { annualFeedIn ->
                val difference = abs(annualFeedIn - totalQuarterHourlyFeedIn)
                difference <= 0.01f * annualFeedIn // 1% tolerance
            } ?: false

            if (isCloseEnough) {
                ValidationResult(Status.VALID, translate("electricity.annualFeedInValid", electricity.annualElectricityFeedIn_kWh, totalQuarterHourlyFeedIn))
            } else {
                ValidationResult(Status.INVALID, translate("electricity.annualFeedInMismatch", electricity.annualElectricityFeedIn_kWh, totalQuarterHourlyFeedIn))
            }
        } else {
            ValidationResult(
                Status.INVALID, translate(
                    "electricity.notEnoughValues",
                    electricity.annualElectricityFeedIn_kWh,
                    electricity.quarterHourlyFeedIn_kWh.values.size
                )
            )
        }
    }

    fun validateQuarterHourlyDeliveryData(electricity: Electricity): ValidationResult {
        val quarterHourlyDelivery_kWh = electricity.quarterHourlyDelivery_kWh
        if (quarterHourlyDelivery_kWh == null) {
            return ValidationResult(
                Status.MISSING_DATA,
                translate("electricity.quarterHourlyDeliveryDataNotProvided")
            )
        }

        return validateTimeSeries(quarterHourlyDelivery_kWh)
    }

    fun validateQuarterHourlyProductionData(electricity: Electricity): ValidationResult {
        electricity.quarterHourlyProduction_kWh?.values ?: return ValidationResult(
            Status.MISSING_DATA,
            translate("electricity.quarterHourlyProductionDataNotProvided")
        )

        return validateTimeSeries(electricity.quarterHourlyProduction_kWh)
    }

    /**
     * peak of delivery should be less than contracted capacity
     */
    fun quarterHourlyDeliveryLowContractedCapacity(electricity: Electricity): ValidationResult {
        val contractedCapacity_kW = electricity.getContractedConnectionCapacityKw()
        if (contractedCapacity_kW == null) {
            return ValidationResult(
                Status.MISSING_DATA, message(
                    en = "Gecontracteerd vermogen levering ontbreek",
                    nl = "Contracted delivery capacity missing",
                )
            )
        }

        if (electricity.quarterHourlyDelivery_kWh == null) {
            return ValidationResult(
                Status.MISSING_DATA, message(
                    en = "Kwartierwaarden levering ontbreek",
                    nl = "Quarter-hourly delivery missing",
                )
            )
        }

        val peakDelivery = electricity.quarterHourlyDelivery_kWh.getPeak()

        return if ( peakDelivery.kW() <= contractedCapacity_kW) {
            ValidationResult(
                Status.VALID, message(
                    en = "Piek van kwartierwaarden levering ${peakDelivery.kWh()} kWh valt binnen gecontracteerd vermogen levering ${contractedCapacity_kW} kW",
                    nl = "Peak of quarter-hourly delivery ${peakDelivery.kWh()} kWh does not exceed contracted capacity ${contractedCapacity_kW} kW",
                )
            )
        } else {
            ValidationResult(
                Status.INVALID, message(
                    nl = "Piek van kwartierwaarden levering ${peakDelivery.kWh()} kWh mag niet hoger zijn dan gecontracteerd vermogen levering $contractedCapacity_kW kW",
                    en = "Peak of quarter-hourly delivery ${peakDelivery.kWh()} kWh should be below contracted capacity $contractedCapacity_kW Kw",
                )
            )
        }
    }

    //peak of feed-in should be less than contracted capacity
    fun quarterFeedInLowContractedCapacity(electricity: Electricity): ValidationResult {
        val contractedFeedInCapacity_kW = electricity.getContractedFeedInCapacityKw()
        if (contractedFeedInCapacity_kW == null) {
            // other validations already set MISSING_DATA for this field
            return ValidationResult(
                Status.NOT_APPLICABLE, message(
                    en = "Gecontracteerd vermogen teruglevering ontbreek",
                    nl = "Contracted feed-in capacity missing",
                )
            )
        }

        if (electricity.quarterHourlyFeedIn_kWh == null) {
            // other validations already set MISSING_DATA for this field
            return ValidationResult(
                Status.NOT_APPLICABLE, message(
                    en = "Kwartierwaarden teruglevering ontbreek",
                    nl = "Quarter-hourly feed-in missing",
                )
            )
        }

        val peakFeedIn = electricity.quarterHourlyFeedIn_kWh.getPeak()
        return if (peakFeedIn.kW() < contractedFeedInCapacity_kW) {
            ValidationResult(
                Status.VALID, message(
                    en = "Piek van kwartierwaarden teruglevering ${peakFeedIn.kWh()} kWh valt binnen gecontracteerd vermogen levering $contractedFeedInCapacity_kW kW",
                    nl = "Peak of quarter-hourly feed-in ${peakFeedIn.kWh()} kWh does not exceed contracted capacity $contractedFeedInCapacity_kW kW",
                )
            )
        } else {
            ValidationResult(
                Status.INVALID, message(
                    nl = "Piek van kwartierwaarden teruglevering ${peakFeedIn.kWh()} kWh mag niet hoger zijn dan gecontracteerd vermogen levering $contractedFeedInCapacity_kW kW",
                    en = "Peak of quarter-hourly feed-in ${peakFeedIn.kWh()} kWh delivery should be below contracted capacity $contractedFeedInCapacity_kW Kw",
                )
            )
        }
    }
}
