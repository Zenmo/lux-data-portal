package com.zenmo.zummon.companysurvey.validation

val translations: Map<Language, Map<String, Map<String, String>>> = mapOf(
    Language.en to mapOf(
        "gridConnection" to mapOf(
            "totalPowerChargePointsValid" to "Total power of charge points is valid",
            "totalPowerChargePointsInvalid" to "Total power of charge points %d exceeds allowed capacity %d",
            "quarterHourlyFeedInLowProductionBatteryPower" to "Every time step in quarter-hourly feed-in is less than or equal to production + battery power",
            "quarterHourlyFeedInHighProductionBatteryPower" to "Time step in quarter-hourly feed-in (%d) is higher than the production and battery power (%d)",
            "incompatibleQuarterHourly" to "Quarter hourly feed-in is not the same length for Quarter hourly production",
            "incompatibleStartTimeQuarterHourly" to "Quarter hourly feed-in start time is not the same for Quarter hourly production",
        ),
        "electricity" to mapOf(
            "kleinverbruikOrGrootverbruikNoDefined" to "Small or large consumption type is not defined",
            "contractedCapacityNotProvided" to "Connection delivery capacity is not provided",
            "physicalCapacityNotProvide" to "Physical connection capacity is not provided",
            "contractedDeliveryCapacityValid" to "Contracted delivery capacity is valid %d",
            "contractedDeliveryCapacityExceeds" to "Contracted delivery capacity %d kW exceeds physical capacity %d kW",
            "connectionFeedInCapacityNotProvide" to "Connection feed in capacity is not provided",
            "feedInLowerPhysicalCapacity" to "Feed-in capacity %d is lower than the physical capacity %d kW",
            "feedInExceedPhysicalCapacity" to "Feed-in capacity %d exceeds physical capacity %d kW",
            "annualElectricityProductionNotProvided" to "Annual electricity production is not provided",
            "annualElectricityFeedInNotProvided" to "Annual electricity feed in is not provided",
            "annualProductionFeedInValid" to "Annual pv production %d is valid, it is more than annual electricity feed-in %d",
            "annualProductionFeedInInvalid" to "Annual PV production %d is less than feed-in %d",

            "quarterHourlyFeedIn_kWh" to "Quarter hourly feed in is not provided",
            "withConnection" to "Electricity with connection",
            "withoutConnection" to "Electricity without connection",
            "quarterHourlyDeliveryDataNotProvided" to "Quarter-hourly delivery data is not provided",

            "quarterHourlyProductionDataNotProvided" to "Quarter-hourly production data is not provided",
            "quarterHourlyProductionCannotBeAllZero" to "Quarter-hourly production data cannot be all zeroes when supply is present",
            "quarterHourlyProductionValid" to "Quarter-hourly production data is valid",


            // quarter
            "notEnoughValues" to "Not enough values for year: needed %d got %d",
            "annualFeedInMismatch" to "Annual feed in (%d) mismatch the total quarter hourly feed in (%d)",
            "annualFeedInMismatch" to "Annual feed in (%d) matches the total quarter hourly feed in (%d)",
            ),
        "grootverbruik" to mapOf(
            "notProvided" to "Large consumption data is not provided",
            "contractedCapacityNotProvided" to "Connection delivery capacity is not provided for large consumption",
            "physicalCapacityNotProvide" to "Physical connection capacity is not provided for large consumption",
            "connectionFeedInCapacityNotProvide" to "Connection feed in capacity is not provided for large consumption",
            "valid" to "Physical connection capacity is within limits (3x80A) for large consumption ",
            "invalid" to "Physical connection capacity %d is below (3x80A) for large consumption",
            "notApplicable" to "Large consumption validations are not applicable",
        ),
        "kleinverbruik" to mapOf(
            "notProvided" to "Small consumption data is not provided",
            "valid" to "Small consumption connection capacity is within limits",
            "exceedsLimit" to "Small consumption connection capacity %d exceeds limit (3x80A)",
            "invalid" to "Small consumption connection capacity is invalid",
            "notApplicable" to "Small consumption validations are not applicable",
        ),
        "storage" to mapOf(
            "batteryCapacityNotProvided" to "Battery Capacity is not provided",
            "batteryPowerNotProvided" to "Battery Power is not provided",
            "batteryWithPowerAndCapacity" to "Battery has Power and Capacity",
            "withoutConnection" to "Without Battery",
        ),
        "naturalGas" to mapOf(
            "annualDeliveryNotProvided" to "Natural gas annual delivery is not provided",
            "withoutConnection" to "Without Natural gas connection",
            "hourlyDeliveryNotProvided" to "Hourly delivery data is not provided",
            "annualGasDeliveryValid" to "Annual gas delivery matches total hourly delivery (%d m³)",
            "annualGasDeliveryMismatch" to "Annual gas delivery %d m³ does not match total hourly delivery %d m³"
        ),

        "transport" to mapOf(
            "carsPowerNotProvided" to "Cars power per charge point is not provided",
            "carsPowerValid" to "Cars power per charge point is valid %d",
            "carsPowerInvalid" to "Cars power per charge point %d is outside the valid range (3..150 kW)",

            "trucksPowerNotProvided" to "Trucks power per charge point is not provided",
            "trucksPowerValid" to "Trucks power per charge point is valid %d",
            "trucksPowerInvalid" to "Trucks power per charge point %d is outside the valid range (3..150 kW)",

            "vansPowerNotProvided" to "Vans power per charge point is not provided",
            "vansPowerValid" to "Vans power per charge point is valid %d",
            "vansPowerInvalid" to "Vans power per charge point %d is outside the valid range (3..150 kW)",

            "distanceCarsNotProvided" to "Cars travel distances is not provided",
            "distanceCarsValid" to "Cars travel distances are valid",
            "distanceCarsInvalid" to "Cars travel distance %d is outside the valid range (5k..100k km)",

            "distanceTrucksNotProvided" to "Trucks travel distances is not provided",
            "distanceTrucksValid" to "Trucks travel distances are valid",
            "distanceTrucksInvalid" to "Trucks travel distance %d is outside the valid range (5k..100k km)",

            "distanceVansNotProvided" to "Vans travel distances is not provided",
            "distanceVansValid" to "Vans travel distances are valid",
            "distanceVansInvalid" to "Vans travel distance %d is outside the valid range (5k..100k km)",

            "electricCarsValid" to "Number of Electric Cars is lower than the total of Cars",
            "electricCarsInvalid" to "Number of electric cars %d exceeds the total number of cars %d",

            "electricTrucksValid" to "Number of Electric Trucks is lower than the total of Trucks",
            "electricTrucksInvalid" to "Number of electric trucks %d exceeds the total number of trucks %d",

            "electricVansValid" to "Number of Electric Vans is lower than the total of Vans",
            "electricVansInvalid" to "Number of electric vans %d exceeds the total number of vans %d",

            "quarterHourlyDeliveryLowContractedCapacityKw" to "Kwartuur levering blijft lager dan de Contractuele CapaciteitKw (%d)",
            "quarterHourlyDeliveryHighContractedCapacityKw" to "Kwartuur levering mag niet hoger zijn dan de Contractuele CapaciteitKw (%d)",
        ),
    ),
    Language.nl to mapOf(
        "gridConnection" to mapOf(
            "totalPowerChargePointsValid" to "Totale laadvermogen is geldig",
            "totalPowerChargePointsInvalid" to "Totale laadvermogen %d overschrijdt de toegestane capaciteit %d",
            "quarterHourlyFeedInLowProductionBatteryPower" to "Elke kwartierwaarde in feed-in is minder dan of gelijk aan de productie plus de batterijcapaciteit.",
            "quarterHourlyFeedInHighProductionBatteryPower" to "De waarde in dit kwartier van de feed-in (%d) is hoger dan de productie en batterijcapaciteit samen (%d).",
            "incompatibleQuarterHourly" to "De kwartierwaarden van de feed-in en productie hebben niet dezelfde lengte.",
            "incompatibleStartTimeQuarterHourly" to "De starttijd van de kwartierwaarden voor feed-in komt niet overeen met die van de productie.",
            "pvInstalledLow" to "PV geïnstalleerd vermogen blijft lager dan 5000 kW",
            "pvInstalledHigh" to "PV geïnstalleerd vermogen mag niet groter zijn dan 5000 kW",
        ),
        "electricity" to mapOf(
            "kleinverbruikOrGrootverbruikNoDefined" to "Klein- of grootverbruikstype is niet gedefinieerd",
            "contractedCapacityNotProvided" to "Gecontracteerd vermogen ontbreekt",
            "physicalCapacityNotProvide" to "Fysieke aansluitcapaciteit ontbreekt",
            "contractedDeliveryCapacityValid" to "Gecontracteerde aansluitcapaciteit %d is geldig",
            "contractedDeliveryCapacityExceeds" to "Gecontracteerde aansluitcapaciteit %d kW overschrijdt fysieke capaciteit %d kW",
            "connectionFeedInCapacityNotProvide" to "Gecontracteerd transportvermogen voor teruglevering ontbreekt",
            "feedInLowerPhysicalCapacity" to "Gecontracteerd transportvermogen teruglevering %d valt binnen de fysieke capaciteit %d kW",
            "feedInExceedPhysicalCapacity" to "Gecontracteerd transportvermogen teruglevering %d overschrijdt fysieke capaciteit %d kW",

            "annualElectricityProductionNotProvided" to "Jaartotaal elektriciteitsproductie ontbreekt",
            "annualElectricityFeedInNotProvided" to "Jaartotaal teruglevering van elektriciteit ontbreekt",
            "annualProductionFeedInValid" to "Jaartotaal PV-productie %d is geldig, het is meer dan de jaarlijkse teruglevering %d",
            "annualProductionFeedInInvalid" to "Jaartotaal PV-productie %d is minder dan teruglevering %d",
            "quarterHourlyDeliveryDataNotProvided" to "Kwartiergegevens voor levering zijn niet opgegeven",
            "quarterHourlyDeliveryDataValid" to "Kwartiergegevens voor levering hebben geen gaten boven de limiet",

            "quarterHourlyProductionDataNotProvided" to "Kwartiergegevens van de productie zijn niet opgegeven",
            "quarterHourlyProductionDataHolesExceed" to "Kwartiergegevens van de productie hebben een gat van %d uur, dat de toegestane limiet overschrijdt",

            "quarterHourlyProductionCannotBeAllZero" to "Kwartierwaarden voor productie mogen niet allemaal nul zijn als er opwek is",
            "quarterHourlyProductionValid" to "Kwartierwaarden voor productie zijn geldig",
        ),
        "grootverbruik" to mapOf(
            "notProvided" to "Data voor grootverbruik ontbreekt",
            "contractedCapacityNotProvided" to "Gecontracteerd vermogen voor grootverbruik ontbreekt",
            "physicalCapacityNotProvide" to "Fysieke aansluitcapaciteit voor grootverbruik ontbreekt",
            "connectionFeedInCapacityNotProvide" to "Gecontracteerd transportvermogen teruglevering voor grootverbruik ontbreekt",
            "valid" to "Fysieke aansluitcapaciteit voor grootverbruik voldoet aan het minimum (3x80A)",
            "invalid" to "Fysieke aansluitcapaciteit %d is lager dan het minimum (3x80A) voor grootverbruik",
            "notApplicable" to "Validaties voor grootverbruik zijn niet van toepassing",
        ),
        "kleinverbruik" to mapOf(
            "notProvided" to "Data voor kleinverbruik ontbreekt",
            "valid" to "Kleinverbruik-aansluitcapaciteit is binnen de limiet",
            "exceedsLimit" to "Kleinverbruik-aansluitcapaciteit %d overschrijdt de limiet (3x80A)",
            "invalid" to "Kleinverbruik-aansluitcapaciteit is ongeldig",
            "notApplicable" to "Validaties voor kleinverbruik zijn niet van toepassing",
        ),
        "storage" to mapOf(
            "batteryCapacityNotProvided" to "Batterijcapaciteit is niet opgegeven",
            "batteryPowerNotProvided" to "Batterijvermogen is niet opgegeven",
            "batteryWithPowerAndCapacity" to "Batterij heeft Vermogen en Capaciteit",
            "withoutConnection" to "Zonder Batterij"
        ),
        "naturalGas" to mapOf(
            "annualDeliveryNotProvided" to "Jaarlijkse gaslevering is niet opgegeven",
            "hourlyDeliveryNotProvided" to "Uurleveringsgegevens zijn niet opgegeven",
            "annualGasDeliveryValid" to "Jaarlijkse gaslevering komt overeen met de totale uurlevering",
            "annualGasDeliveryMismatch" to "Jaarlijkse gaslevering %d m³ komt niet overeen met de totale uurlevering %d m³",
            "withoutConnection" to "Zonder aardgasaansluiting"
        ),
        "transport" to mapOf(
            "carsPowerNotProvided" to "Vermogen per laadpunt voor auto's ontbreekt",
            "carsPowerValid" to "Vermogen per laadpunt voor auto's is geldig %d",
            "carsPowerInvalid" to "Vermogen per laadpunt voor auto's %d ligt buiten het toegestane bereik (3..150 kW)",

            "trucksPowerNotProvided" to "Vermogen per laadpunt voor vrachtwagens ontbreekt",
            "trucksPowerValid" to "Vermogen per laadpunt voor vrachtwagens is geldig %d",
            "trucksPowerInvalid" to "Vermogen per laadpunt voor vrachtwagens %d ligt buiten het toegestane bereik (3..150 kW)",

            "vansPowerNotProvided" to "Vermogen per laadpunt voor bestelwagens ontbreekt",
            "vansPowerValid" to "Vermogen per laadpunt voor bestelwagens is geldig %d",
            "vansPowerInvalid" to "Vermogen per laadpunt voor bestelwagens %d ligt buiten het toegestane bereik (3..150 kW)",

            "distanceCarsNotProvided" to "Afstand afgelegd door auto's ontbreekt",
            "distanceCarsValid" to "Afstand afgelegd door auto's is geldig",
            "distanceCarsInvalid" to "Afstand afgelegd door auto's %d ligt buiten het toegestane bereik (5k..100k km)",

            "distanceTrucksNotProvided" to "Afstand afgelegd door vrachtwagens ontbreekt",
            "distanceTrucksValid" to "Afstand afgelegd door vrachtwagens is geldig",
            "distanceTrucksInvalid" to "Afstand afgelegd door vrachtwagens %d ligt buiten het toegestane bereik (5k..100k km)",

            "distanceVansNotProvided" to "Afstand afgelegd door bestelwagens ontbreekt",
            "distanceVansValid" to "Afstand afgelegd door bestelwagens is geldig",
            "distanceVansInvalid" to "Afstand afgelegd door bestelwagens %d ligt buiten het toegestane bereik (5k..100k km)",

            "electricCarsValid" to "Aantal elektrische auto's valt binnen het totale aantal auto's",
            "electricCarsInvalid" to "Aantal elektrische auto's %d overschrijdt het totale aantal auto's %d",

            "electricTrucksValid" to "Aantal elektrische vrachtwagens valt binnen het totale aantal vrachtwagens",
            "electricTrucksInvalid" to "Aantal elektrische vrachtwagens %d overschrijdt het totale aantal vrachtwagens %d",

            "electricVansValid" to "Aantal elektrische bestelwagens valt binnen het totale aantal bestelwagens",
            "electricVansInvalid" to "Aantal elektrische bestelwagens %d overschrijdt het totale aantal bestelwagens %d",
        ),
    )
)

/**
 * Utility function to display a message in the currently active language.
 * Intended to replace translation keys, because it's harder to miss a translation,
 * and it leads to better locality between both translations and between the messages and the code.
 */
fun message(nl: String, en: String): String =
    when (getLanguage()) {
        Language.en -> en
        Language.nl -> nl
    }

// Translation function with fallback to English
fun translate(key: String, vararg args: Any?): String {
    val (module, translationKey) = key.split(".").let { it[0] to it[1] }

    val translation = translations[getLanguage()]?.get(module)?.get(translationKey)
        ?: translations[Language.en]?.get(module)?.get(translationKey)
        ?: key // Fallback to key itself if translation is missing
    return if (args.isEmpty()) {
        translation
    } else {
        replacePlaceholders(translation, *args)
    }
}

fun replacePlaceholders(template: String, vararg args: Any?): String {
    var result = template
    args.forEach { arg ->
        result = result.replaceFirst("%d", arg.toString())
    }
    return result
}
