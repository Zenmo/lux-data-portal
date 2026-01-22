package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.GridConnection
import com.zenmo.zummon.companysurvey.toHours
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

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
        val feedInTimeSeries = gridConnection.electricity.quarterHourlyFeedIn_kWh
        val productionTimeSeries = gridConnection.electricity.quarterHourlyProduction_kWh

        if (feedInTimeSeries == null || productionTimeSeries == null) {
            return ValidationResult(
                Status.NOT_APPLICABLE,
                message(
                    nl = "Geen kwartierwaarden invoeding of opwek",
                    en = "No quarter-hourly feed-in or production"
                )
            )
        }

        // Ensure both time series starts at the same time
        if (feedInTimeSeries.start != productionTimeSeries.start) {
            return ValidationResult(
                Status.INVALID,
                translate("gridConnection.incompatibleStartTimeQuarterHourly")
            )
        }

        // Ensure both time series have the same length for comparison
        if (feedInTimeSeries.values.size != productionTimeSeries.values.size) {
            return ValidationResult(
                Status.INVALID,
                translate("gridConnection.incompatibleQuarterHourly")
            )
        }

        val batteryPowerKw = (gridConnection.storage.batteryPowerKw ?: 0.0).toFloat()
        val maxBatteryDischarge = batteryPowerKw * feedInTimeSeries.timeStep.toHours()

        // Check each value in the feed-in time series is <= the corresponding production value
        feedInTimeSeries.values.forEachIndexed { index, feedInKwh ->
            val productionKwh = productionTimeSeries.values[index]
            val maxPossibleFeedIn = productionKwh + maxBatteryDischarge

            if (feedInKwh > maxPossibleFeedIn) {
                val timeStamp = feedInTimeSeries.start.plus(index, feedInTimeSeries.timeStep, TimeZone.of("Europe/Amsterdam"))

                return ValidationResult(Status.INVALID, message(
                    nl = "De invoeding van $feedInKwh kWh op $timeStamp is hoger dan de opwek ($productionKwh kWh) plus de maximale ontlading van de batterij ($maxBatteryDischarge kWh).",
                    en = "The feed-in of $feedInKwh kWh at $timeStamp is higher that production ($productionKwh kWh) plus the maximum discharge of the battery ($maxBatteryDischarge kWh)."
                ))
            }
        }

        return ValidationResult(Status.VALID, message(
            nl = "Elke kwartierwaarde van de invoeding is minder dan of gelijk aan de opwek plus het vermogen van de batterij.",
            en = "Every value in quarter-hourly feed-in is less than or equal to production + battery power"
        ))
    }

    /**
     * Sanity check: PV installed power should not be larger dan 5000kW
     */
    fun validatePvInstalled(gridConnection: GridConnection): ValidationResult {
        if (gridConnection.supply.hasSupply == false) {
            return ValidationResult(
                Status.NOT_APPLICABLE, message(
                    en = "No power generation",
                    nl = "Geen opwek",
                )
            )
        }

        val thresholdKwp = 5000.0
        val pvInstalledKwp = gridConnection.supply.pvInstalledKwp?.toDouble() ?: 0.0

        return if (pvInstalledKwp > thresholdKwp) {
            ValidationResult(
                Status.INVALID, message(
                    nl = "Geïnstalleerd vermogen zonnepanelen $pvInstalledKwp kW is hoger dan $thresholdKwp kW.",
                    en = "Installed PV power $pvInstalledKwp kW is higher than $thresholdKwp kW."
                )
            )
        } else {
            ValidationResult(
                Status.VALID, message(
                    nl = "Geïnstalleerd vermogen zonnepanelen $pvInstalledKwp kW is lager dan $thresholdKwp kW.",
                    en = "Installed PV power is $pvInstalledKwp kW lower than $thresholdKwp kW."
                )
            )
        }
    }
}
