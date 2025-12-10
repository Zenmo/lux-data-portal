package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.TimeSeries
import com.zenmo.zummon.companysurvey.TimeSeriesType
import kotlin.time.Duration.Companion.days

private val maxGapLength = 4.days

private fun validMessage(type: TimeSeriesType): String = message(
    nl = "${type.nlDisplayName} heeft een jaar aan datapunten met maximaal 4 dagen ontbrekend",
    en = "${type.enDisplayName} has a year worth of datapoints with no gaps longer than 4 days",
)

private fun notEnoughDataPointsMessage(type: TimeSeriesType, numRequired: Int, numPresent: Int): String = message(
    nl = "${type.nlDisplayName} moet een jaar aan waarden hebben. Verwacht $numRequired datapunten, zijn er $numPresent.",
    en = "${type.enDisplayName} must have a year worth of values. Expected $numRequired datapoints, found $numPresent.",
)

private fun gapsLongerThanFourDaysMessage(type: TimeSeriesType): String = message(
    nl = "${type.nlDisplayName} heeft ontbrekende of 0-waarden meer voor dan ${maxGapLength.inWholeDays} dagen.",
    en = "${type.enDisplayName} has missing or zeroed data for more than ${maxGapLength.inWholeDays} days.",
)

/**
 * Generic validation for all types of time series.
 * On top of this there are specific validators for each type.
 * The called validates beforehand that the time series is present (= not null).
 */
fun validateTimeSeries(timeSeries: TimeSeries): ValidationResult {
    if (!timeSeries.hasNumberOfValuesForOneYear()) {
        return ValidationResult(
            Status.INVALID,
            notEnoughDataPointsMessage(timeSeries.type, timeSeries.numValuesNeededForFullYear(), timeSeries.values.size)
        )
    }

    if (timeSeries.getLongestGapDuration() > maxGapLength && timeSeries.type != TimeSeriesType.ELECTRICITY_FEED_IN) {
        return ValidationResult(
            Status.INVALID,
            gapsLongerThanFourDaysMessage(timeSeries.type)
        )
    }

    return ValidationResult(Status.VALID, validMessage(timeSeries.type))
}
