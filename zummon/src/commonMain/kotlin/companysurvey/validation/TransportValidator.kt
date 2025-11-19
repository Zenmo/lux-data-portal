package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.Transport

class TransportValidator {
    fun validate(transport: Transport): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()

        results.add(validatePowerPerChargeCars(transport))
        results.add(validatePowerPerChargeTrucks(transport))
        results.add(validatePowerPerChargeVans(transport))

        results.add(validateTravelDistanceCar(transport))
        results.add(validateTravelDistanceTruck(transport))
        results.add(validateTravelDistanceVan(transport))

        results.add(validateTotalElectricCars(transport))
        results.add(validateTotalElectricTrucks(transport))
        results.add(validateTotalElectricVans(transport))

        return results
    }

    // Validator for power per charge point in range 3..150 kW
    fun validatePowerPerChargeCars(transport: Transport): ValidationResult {
        val powerPerChargePointCars = transport.cars.powerPerChargePointKw

        return when {
            powerPerChargePointCars == null -> ValidationResult(Status.NOT_APPLICABLE, translate("transport.carsPowerNotProvided"))
            powerPerChargePointCars in 3.0..150.0 -> ValidationResult(Status.VALID, translate("transport.carsPowerValid", powerPerChargePointCars))
            else -> ValidationResult(Status.INVALID, translate("transport.carsPowerInvalid", powerPerChargePointCars))
        }
    }

    fun validatePowerPerChargeTrucks(transport: Transport): ValidationResult {
        val powerPerChargePointTrucks = transport.trucks.powerPerChargePointKw

        return when {
            powerPerChargePointTrucks == null -> ValidationResult(Status.NOT_APPLICABLE, translate("transport.trucksPowerNotProvided"))
            powerPerChargePointTrucks in 3.0..150.0 -> ValidationResult(Status.VALID, translate("transport.trucksPowerValid", powerPerChargePointTrucks))
            else -> ValidationResult(Status.INVALID, translate("transport.trucksPowerInvalid", powerPerChargePointTrucks))
        }
    }

    fun validatePowerPerChargeVans(transport: Transport): ValidationResult {
        val powerPerChargePointVans = transport.vans.powerPerChargePointKw

        return when {
            powerPerChargePointVans == null -> ValidationResult(Status.NOT_APPLICABLE, translate("transport.vansPowerNotProvided"))
            powerPerChargePointVans in 3.0..150.0 -> ValidationResult(Status.VALID, translate("transport.vansPowerValid", powerPerChargePointVans))
            else -> ValidationResult(Status.INVALID, translate("transport.vansPowerInvalid", powerPerChargePointVans))
        }
    }

    // Validator for vehicle travel distance in range 5k..100k km
    fun validateTravelDistanceCar(transport: Transport): ValidationResult {
        val travelDistanceCars = transport.cars.annualTravelDistancePerCarKm

        return when {
            travelDistanceCars == null -> ValidationResult(Status.MISSING_DATA, translate("transport.distanceCarsNotProvided"))
            travelDistanceCars in 5000..100000 -> ValidationResult(Status.VALID, translate("transport.distanceCarsValid"))
            else -> ValidationResult(Status.INVALID, translate("transport.distanceCarsInvalid", travelDistanceCars))
        }
    }

    fun validateTravelDistanceTruck(transport: Transport): ValidationResult {
        val travelDistanceTrucks = transport.trucks.annualTravelDistancePerTruckKm

        return when {
            travelDistanceTrucks == null -> ValidationResult(Status.MISSING_DATA, translate("transport.distanceTrucksNotProvided"))
            travelDistanceTrucks in 5000..100000 -> ValidationResult(Status.VALID, translate("transport.distanceTrucksValid"))
            else -> ValidationResult(Status.INVALID, translate("transport.distanceTrucksInvalid", travelDistanceTrucks))
        }
    }

    fun validateTravelDistanceVan(transport: Transport): ValidationResult {
        val travelDistanceVans = transport.vans.annualTravelDistancePerVanKm

        return when {
            travelDistanceVans == null -> ValidationResult(Status.MISSING_DATA, translate("transport.distanceVansNotProvided"))
            travelDistanceVans in 5000..100000 -> ValidationResult(Status.VALID, translate("transport.distanceVansValid"))
            else -> ValidationResult(Status.INVALID, translate("transport.distanceVansInvalid", travelDistanceVans))
        }
    }

    // Validator for number of electric vehicles should be less than or equal to total number of vehicles
    fun validateTotalElectricCars(transport: Transport): ValidationResult {
        return when {
            (transport.cars.numElectricCars ?: 0) > (transport.cars.numCars ?: 0) -> ValidationResult(Status.INVALID, translate("transport.electricCarsInvalid", transport.cars.numElectricCars, transport.cars.numCars))
            else -> ValidationResult(Status.VALID, translate("transport.electricCarsValid"))
        }
    }

    fun validateTotalElectricTrucks(transport: Transport): ValidationResult {
        return when {
            (transport.trucks.numElectricTrucks ?: 0) > (transport.trucks.numTrucks ?: 0) -> ValidationResult(Status.INVALID, translate("transport.electricCTrucksInvalid", transport.trucks.numTrucks, transport.trucks.numTrucks))
            else -> ValidationResult(Status.VALID, translate("transport.electricTrucksValid"))
        }
    }

    fun validateTotalElectricVans(transport: Transport): ValidationResult {
        return when {
            ((transport.vans.numElectricVans ?: 0) > (transport.vans.numVans ?: 0)) -> ValidationResult(Status.INVALID, translate("transport.electricVansInvalid", transport.vans.numElectricVans, transport.vans.numVans))
            else -> ValidationResult(Status.VALID, translate("transport.electricVansValid"))
        }
    }
}
