package com.zenmo.orm.companysurvey

import com.zenmo.zummon.companysurvey.*
import kotlinx.datetime.DateTimeUnit
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

val mockSurvey = createMockSurvey()

fun createHourlyValues(duration: Duration) = sequence {
    while (true) {
        yield(1.2f)
        yield(2.2f)
        yield(3.2f)
        yield(4.2f)
    }
}.take((duration / 1.hours).roundToInt()).toList().toFloatArray()

fun createQuarterHourlyValues(duration: Duration) = sequence {
    while (true) {
        yield(1.2f)
        yield(2.2f)
        yield(3.2f)
        yield(4.2f)
    }
}.take((duration / 15.minutes).roundToInt()).toList().toFloatArray()

fun createMockSurvey(
    projectName: String = "Project",
    timeSeriesDuration: Duration = 4.hours,
) = Survey(
    companyName = "Zenmo",
    zenmoProject = projectName,
    personName = "John Doe",
    email = "john@example.com",
    dataSharingAgreed = true,
    includeInSimulation = true,
    addresses = listOf(
        Address(
            id = Uuid.generateV7(),
            street = "Kerkstraat",
            houseNumber = 35,
            houseLetter = "A",
            houseNumberSuffix = "100",
            postalCode = "1234AB",
            city = "Amsterdam",
            gridConnections = listOf(
                GridConnection(
                    electricity = Electricity(
                        hasConnection = true,
                        annualElectricityDelivery_kWh = 1000,
                        annualElectricityFeedIn_kWh = 2000,
                        ean = "123456789012345678",
                        quarterHourlyValuesFiles = listOf(
                            File(
                                blobName = "qwerty-kwartierwaarden-2021.csv",
                                originalName = "kwartierwaarden-2021.csv",
                                contentType = "text/csv",
                                size = 1000,
                            ),
                            File(
                                blobName = "qwerty-kwartierwaarden-2022.csv",
                                originalName = "kwartierwaarden-2022.csv",
                                contentType = "text/csv",
                                size = 1000,
                            ),
                        ),
                        kleinverbruikOrGrootverbruik = KleinverbruikOrGrootverbruik.KLEINVERBRUIK,
                        grootverbruik = CompanyGrootverbruik(
                            contractedConnectionDeliveryCapacity_kW = 100,
                            contractedConnectionFeedInCapacity_kW = 200,
                            physicalCapacityKw = 300,
                        ),
                        kleinverbruik = CompanyKleinverbruik(
                            connectionCapacity = KleinverbruikElectricityConnectionCapacity._3x63A,
                            consumptionProfile = KleinverbruikElectricityConsumptionProfile.TWO,
                        ),
                        authorizationFile = File(
                            blobName = "authorization.pdf",
                            originalName = "Authorization.pdf",
                            contentType = "application/pdf",
                            size = 10000,
                        ),
                        gridExpansion = GridExpansion(
                            hasRequestAtGridOperator = true,
                            requestedKW = 200,
                            reason = "Mooaar power!",
                        ),
                        quarterHourlyDelivery_kWh = TimeSeries(
                            type = TimeSeriesType.ELECTRICITY_DELIVERY,
                            start = kotlinx.datetime.Instant.parse("2022-01-01T00:00:00+01"),
                            values = createQuarterHourlyValues(timeSeriesDuration),
                        ),
                        quarterHourlyFeedIn_kWh = TimeSeries(
                            type = TimeSeriesType.ELECTRICITY_FEED_IN,
                            start = kotlinx.datetime.Instant.parse("2022-01-01T00:00:00+01"),
                            values = createQuarterHourlyValues(timeSeriesDuration),
                        ),
                        quarterHourlyProduction_kWh = TimeSeries(
                            type = TimeSeriesType.ELECTRICITY_PRODUCTION,
                            start = kotlinx.datetime.Instant.parse("2022-01-01T00:00:00+01"),
                            values = createQuarterHourlyValues(timeSeriesDuration),
                        ),
                        annualElectricityProduction_kWh = 3000,
                    ),
                    supply = Supply(
                        hasSupply = true,
                        pvInstalledKwp = 100,
                        pvOrientation = PVOrientation.SOUTH,
                        pvPlanned = true,
                        pvPlannedKwp = 200,
                        pvPlannedOrientation = PVOrientation.EAST_WEST,
                        pvPlannedYear = 2022,
                        windInstalledKw = 300f,
                        windPlannedKw = 400f,
                        otherSupply = "Other supply",
                        missingPvReason = MissingPvReason.OTHER,
                    ),
                    naturalGas = NaturalGas(
                        ean = "123456789012345678",
                        hasConnection = true,
                        annualDelivery_m3 = 3500,
                        hourlyValuesFiles = listOf(
                            File(
                                blobName = "qwerty-uurwaarden-2022.csv",
                                originalName = "uurwaarden-2022.csv",
                                contentType = "text/csv",
                                size = 1000,
                            ),
                        ),
                        percentageUsedForHeating = 50,
                        hourlyDelivery_m3 = TimeSeries(
                            type = TimeSeriesType.GAS_DELIVERY,
                            start = kotlinx.datetime.Instant.parse("2022-01-01T00:00:00+01"),
                            timeStep = DateTimeUnit.HOUR,
                            values = createHourlyValues(timeSeriesDuration),
                        )
                    ),
                    heat = Heat(
                        heatingTypes = listOf(HeatingType.GAS_BOILER, HeatingType.DISTRICT_HEATING),
                        sumGasBoilerKw = 28.8f,
                        sumHeatPumpKw = 0f,
                        sumHybridHeatPumpElectricKw = 0f,
                        annualDistrictHeatingDelivery_GJ = 300f,
                        localHeatExchangeDescription = "Local heat exchange description",
                        hasUnusedResidualHeat = false,
                        heatPumpElectricityConsumptionTimeSeries_kWh = TimeSeries(
                            type = TimeSeriesType.HEAT_PUMP_ELECTRICITY_CONSUMPTION,
                            start = kotlinx.datetime.Instant.parse("2022-01-01T00:00:00+01"),
                            values = floatArrayOf(1.15f, 2.15f, 3.15f, 4.15f),
                        ),
                        heatDeliveryTimeSeries_kWh = TimeSeries(
                            type = TimeSeriesType.HEAT_DELIVERY,
                            start = kotlinx.datetime.Instant.parse("2022-01-01T00:00:00+01"),
                            values = floatArrayOf(2.4f, 4.4f, 6.4f, 8.4f),
                        ),
                    ),
                    storage = Storage(
                        hasBattery = false,
                        batteryCapacityKwh = 0f,
                        batteryPowerKw = 0f,
                        batterySchedule = "Battery schedule",
                        hasPlannedBattery = true,
                        plannedBatteryCapacityKwh = 100f,
                        plannedBatteryPowerKw = 10f,
                        plannedBatterySchedule = "Planned battery schedule",
                        hasThermalStorage = true,
                    ),
                    mainConsumptionProcess = "Main consumption process",
                    electrificationPlans = "Electrification plans",
                    consumptionFlexibility = "Consumption flexibility",
                    energyOrBuildingManagementSystemSupplier = "EnergyBrothers",
                    surveyFeedback = "Survey feedback",
                    transport = Transport(
                        hasVehicles = false,
                        numDailyCarAndVanCommuters = 14,
                        numDailyCarVisitors = 5,
                        numCommuterAndVisitorChargePoints = 2,
                        cars = Cars(
                            numCars = 2,
                            numElectricCars = 0,
                            numChargePoints = 0,
                            powerPerChargePointKw = 0f,
                            annualTravelDistancePerCarKm = 0,
                            numPlannedElectricCars = 0,
                            numPlannedHydrogenCars = 2,
                        ),
                        trucks = Trucks(
                            numTrucks = 5,
                            numElectricTrucks = 0,
                            numChargePoints = 0,
                            powerPerChargePointKw = 0f,
                            annualTravelDistancePerTruckKm = 0,
                            numPlannedElectricTrucks = 0,
                            numPlannedHydrogenTrucks = 2,
                        ),
                        vans = Vans(
                            numVans = 2,
                            numElectricVans = 0,
                            numChargePoints = 0,
                            powerPerChargePointKw = 0f,
                            annualTravelDistancePerVanKm = 0,
                            numPlannedElectricVans = 0,
                            numPlannedHydrogenVans = 2,
                        ),
                        agriculture = Agriculture(
                            numTractors = 3,
                            annualDieselUsage_L = 4200.1,
                            dieselUsageTimeSeries =  TimeSeries(
                                type = TimeSeriesType.AGRICULTURE_DIESEL_CONSUMPTION,
                                start = kotlinx.datetime.Instant.parse("2021-01-01T00:00:00+01"),
                                timeStep = DateTimeUnit.YEAR,
                                values = floatArrayOf(800.0f, 900.0f),
                            )
                        ),
                        otherVehicles = OtherVehicles(
                            hasOtherVehicles = true,
                            description = "Other vehicles description",
                        ),
                    ),
                    pandIds = setOf(
                        PandID("1234567890123456"),
                        PandID("6543210987654321"),
                    )
                )
            ),
        )
    )
)
