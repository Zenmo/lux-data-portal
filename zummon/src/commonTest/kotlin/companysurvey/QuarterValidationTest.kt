package companysurvey

import com.zenmo.zummon.companysurvey.*
import com.zenmo.zummon.companysurvey.validation.ElectricityValidator
import com.zenmo.zummon.companysurvey.validation.GridConnectionValidator
import com.zenmo.zummon.companysurvey.validation.Status
import com.zenmo.zummon.companysurvey.validation.ValidationResult
import com.zenmo.zummon.companysurvey.validation.translate
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class QuarterValidationTest {
    val electricityValidator = ElectricityValidator()
    val gridConnectionValidator = GridConnectionValidator()
    val numValuesPerYear = 365 * 4 * 24

    @Test
    fun validateQuarterHourlyDeliveryNoProvide() {
        val electricity = Electricity(
            quarterHourlyDelivery_kWh = null
        )
        val result = electricityValidator.validateQuarterHourlyDeliveryData(electricity)
        assertEquals(Status.MISSING_DATA, result.status)
        assertEquals("Quarter-hourly delivery data is not provided", result.message)
    }

    @Test
    fun validateQuarterHourlyDeliveryDataValidNoHoles() {
        val electricity = Electricity(
            quarterHourlyDelivery_kWh = TimeSeries(
                type = TimeSeriesType.ELECTRICITY_DELIVERY,
                start = Instant.parse("2022-01-01T00:00:00Z"),
                values = generateSequence { 1.0f }.take(numValuesPerYear).toList().toFloatArray()
            )
        )
        val result = electricityValidator.validateQuarterHourlyDeliveryData(electricity)
        assertEquals(Status.VALID, result.status)
        assertEquals(
            "Quarter-hourly electricity delivery has a year worth of datapoints with no gaps longer than 4 days",
            result.message
        )
    }

    @Test
    fun validateQuarterHourlyDeliveryDataSmallGaps() {
        val electricity = Electricity(
            quarterHourlyDelivery_kWh = TimeSeries(
                type = TimeSeriesType.ELECTRICITY_DELIVERY,
                start = Instant.parse("2022-01-01T00:00:00Z"),
                values = sequence {
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                    // A small gap which should pass validation
                    yieldAll(generateSequence { 0.0f }.take(10))
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                }.toList().toFloatArray()
            )
        )
        val result = electricityValidator.validateQuarterHourlyDeliveryData(electricity)
        assertEquals(Status.VALID, result.status)
        assertEquals(
            "Quarter-hourly electricity delivery has a year worth of datapoints with no gaps longer than 4 days",
            result.message
        )
    }

    @Test
    fun validateQuarterHourlyDeliveryDataGapsExceed() {
        val electricity = Electricity(
            quarterHourlyDelivery_kWh = TimeSeries(
                type = TimeSeriesType.ELECTRICITY_DELIVERY,
                start = Instant.parse("2022-01-01T00:00:00Z"),
                values = sequence {
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                    // 385 nulls, exceeding limit
                    yieldAll(generateSequence { 0.0f }.take(385))
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                }.toList().toFloatArray()
            )
        )
        val result = electricityValidator.validateQuarterHourlyDeliveryData(electricity)
        assertEquals(Status.INVALID, result.status)
        assertEquals("Quarter-hourly electricity delivery has missing or zeroed data for more than 4 days.", result.message)
    }

    @Test
    fun validateQuarterHourlyProductionDataInvalidGaps() {
        val electricity = Electricity(
            quarterHourlyDelivery_kWh = TimeSeries(
                type = TimeSeriesType.ELECTRICITY_PRODUCTION,
                start = Instant.parse("2022-01-01T00:00:00Z"),
                values = sequence {
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                    // 385 nulls, exceeding limit
                    yieldAll(generateSequence { 0.0f }.take(385))
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                }.toList().toFloatArray()
            )
        )
        val result = electricityValidator.validateQuarterHourlyDeliveryData(electricity)
        assertEquals(Status.INVALID, result.status)
        assertEquals(
            "Quarter-hourly electricity production has missing or zeroed data for more than 4 days.",
            result.message
        )
    }

    @Test
    fun validateQuarterHourlyDeliveryDataExactLimit() {
        val electricity = Electricity(
            quarterHourlyDelivery_kWh = TimeSeries(
                type = TimeSeriesType.ELECTRICITY_DELIVERY,
                start = Instant.parse("2022-01-01T00:00:00Z"),
                values = sequence {
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                    // 384 nulls, at the limit
                    yieldAll(generateSequence { 0.0f }.take(384))
                    yieldAll(generateSequence { 1.0f }.take(numValuesPerYear / 2))
                }.toList().toFloatArray()
            )
        )
        val result = electricityValidator.validateQuarterHourlyDeliveryData(electricity)
        assertEquals(Status.VALID, result.status)
        assertEquals("Quarter-hourly electricity delivery has a year worth of datapoints with no gaps longer than 4 days", result.message)
    }

    @Test
    fun testQuarterHourlyFeedInLowProductionBatteryPower_ValidData() {
        // Set up mock data with valid feed-in, production, and battery power
        val gridConnection = GridConnection(
            electricity = Electricity(
                quarterHourlyFeedIn_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(1.0f, 1.5f, 2.0f)),
                quarterHourlyProduction_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(9.0f, 7.5f, 6.0f))
            ),
            storage = Storage(batteryPowerKw = 0.5f)
        )
        
        val result = gridConnectionValidator.quarterHourlyFeedInLowProductionBatteryPower(gridConnection)

        assertEquals(Status.VALID, result.status)
    }

    @Test
    fun testQuarterHourlyFeedInLowProductionBatteryPower_InvalidStartTime() {
        // Set up mock data where start times are different
        val gridConnection = GridConnection(
            electricity = Electricity(
                quarterHourlyFeedIn_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(1.0f, 1.5f, 2.0f)),
                quarterHourlyProduction_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T01:00:00Z"), values = floatArrayOf(1.0f, 1.5f, 2.0f))
            ),
            storage = Storage(batteryPowerKw = 0.5f)
        )
        
        val result = gridConnectionValidator.quarterHourlyFeedInLowProductionBatteryPower(gridConnection)

        // Assert the result is invalid and the message matches the expected translation
        assertEquals(Status.INVALID, result.status)
        assertEquals(translate("gridConnection.incompatibleStartTimeQuarterHourly"), result.message)
    }

    @Test
    fun testQuarterHourlyFeedInLowProductionBatteryPower_InvalidLength() {
        // Set up mock data with mismatched lengths of feed-in and production values
        val gridConnection = GridConnection(
            electricity = Electricity(
                quarterHourlyFeedIn_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(1.0f, 1.5f, 2.0f)),
                quarterHourlyProduction_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(1.0f, 1.5f))
            ),
            storage = Storage(batteryPowerKw = 0.5f)
        )
        val result = gridConnectionValidator.quarterHourlyFeedInLowProductionBatteryPower(gridConnection)

        // Assert the result is invalid and the message matches the expected translation
        assertEquals(Status.INVALID, result.status)
        assertEquals(translate("gridConnection.incompatibleQuarterHourly"), result.message)
    }

    @Test
    fun testQuarterHourlyFeedInLowProductionBatteryPower_HighFeedIn() {
        // Set up mock data with a high feed-in value
        val gridConnection = GridConnection(
            electricity = Electricity(
                quarterHourlyFeedIn_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(2.5f, 3.0f, 3.5f)),
                quarterHourlyProduction_kWh = TimeSeries(type= TimeSeriesType.ELECTRICITY_DELIVERY, start = Instant.parse("2024-01-01T00:00:00Z"), values = floatArrayOf(1.0f, 1.5f, 2.0f))
            ),
            storage = Storage(batteryPowerKw = 0.5f)
        )

        val result = gridConnectionValidator.quarterHourlyFeedInLowProductionBatteryPower(gridConnection)

        assertEquals(Status.INVALID, result.status)
    }
}
