package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.GridConnection

class GridConnectionValidator : Validator<GridConnection> {
    override fun validate(gridConnection: GridConnection): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()

        results.addAll(ElectricityValidator().validate(gridConnection.electricity))
        results.addAll(StorageValidator().validate(gridConnection.storage))
        results.addAll(NaturalGasValidator().validate(gridConnection.naturalGas))
        results.addAll(TransportValidator().validate(gridConnection.transport))
        results.addAll(validateTotalPowerChargePoints(gridConnection))
        results.add(validateQuarterHourlyFeedIn(gridConnection))
        results.add(validateAnnualElectricityProduction(gridConnection))
        results.add(quarterHourlyFeedInLowProductionBatteryPower(gridConnection))
        results.add(validatePvInstalled(gridConnection))

        return results
    }
    // Validator for total charge point power < contracted capacity + battery power
    fun validateTotalPowerChargePoints(gridConnection: GridConnection): List<ValidationResult> {
        val totalPowerChargePoints = listOf(
            gridConnection.transport.cars.powerPerChargePointKw,
            gridConnection.transport.trucks.powerPerChargePointKw,
            gridConnection.transport.vans.powerPerChargePointKw
        ).map { (it ?: 0).toFloat() }.sum()

        val contractedCapacity = (gridConnection.electricity.getContractedConnectionCapacityKw() ?: 0.0).toFloat()
        val batteryPower = (gridConnection.storage.batteryPowerKw ?: 0.0).toFloat()

        return if (totalPowerChargePoints < (contractedCapacity + batteryPower)) {
            listOf(ValidationResult(Status.VALID, translate("gridConnection.totalPowerChargePoints")))
        } else {
            listOf(ValidationResult(Status.INVALID, translate("gridConnection.totalPowerChargePointsInvalid", totalPowerChargePoints, contractedCapacity + batteryPower)))
        }
    }

    //quarter-hourly production should not be all zeroes if hasPv is true
    fun validateQuarterHourlyFeedIn(gridConnection: GridConnection): ValidationResult {
        return when { gridConnection.supply.hasSupply == true && gridConnection.electricity.quarterHourlyProduction_kWh?.values?.all { it == 0f } == true ->
            ValidationResult(Status.INVALID, translate("electricity.quarterHourlyProductionCannotBeAllZero"))
            else ->
                ValidationResult(Status.VALID, translate("electricity.quarterHourlyProductionValid"))
        }
    }

    //hasPV true -> should have annual production
    fun validateAnnualElectricityProduction(gridConnection: GridConnection): ValidationResult {
        return when {
            gridConnection.supply.hasSupply == true && gridConnection.electricity.annualElectricityProduction_kWh == null ->
                ValidationResult(Status.MISSING_DATA, translate("electricity.annualElectricityProductionNotProvided"))
            else ->
                ValidationResult(Status.NOT_APPLICABLE, translate("electricity.withoutConnection"))
        }
    }

    //every time step in quarter-hourly feed-in should be less than or equal to production + battery power
    fun quarterHourlyFeedInLowProductionBatteryPower(gridConnection: GridConnection): ValidationResult {
        val feedIn = gridConnection.electricity.quarterHourlyFeedIn_kWh
        val production = gridConnection.electricity.quarterHourlyProduction_kWh
        val batteryPower = (gridConnection.storage.batteryPowerKw ?: 0.0).toFloat()

        // Ensure both time series starts at the same time
        if (feedIn?.start != production?.start) {
            return ValidationResult(
                Status.INVALID,
                translate("gridConnection.incompatibleStartTimeQuarterHourly")
            )
        }

        // Ensure both time series have the same length for comparison
        if (feedIn?.values?.size != production?.values?.size) {
            return ValidationResult(
                Status.INVALID,
                translate("gridConnection.incompatibleQuarterHourly")
            )
        }

        // Check each value in the feed-in time series is <= the corresponding production value
        feedIn?.values?.forEachIndexed { index, value ->
            production?.values?.let { productionValues ->
                val maxProduction = (productionValues.getOrNull(index) ?: Float.MIN_VALUE) + batteryPower
                if (value > maxProduction) {
                    return ValidationResult(Status.INVALID, translate("gridConnection.quarterHourlyFeedInHighProductionBatteryPower", value, maxProduction))
                }
            }
        }

        return ValidationResult(Status.VALID, translate("gridConnection.quarterHourlyFeedInLowProductionBatteryPower"))
    }

    // PV installed power should not be larger dan 5000kW
    fun validatePvInstalled(gridConnection: GridConnection): ValidationResult {
        return when {
            gridConnection.supply.hasSupply == true && ((gridConnection.supply.pvInstalledKwp ?: 0) < 5000) ->
                ValidationResult(Status.VALID, translate("gridConnection.pvInstalledHigh"))
            else ->
                ValidationResult(Status.INVALID, translate("gridConnection.pvInstalledLow"))
        }
    }
}
