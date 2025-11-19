package com.zenmo.zummon.companysurvey.validation

import com.zenmo.zummon.companysurvey.Storage

class StorageValidator : Validator<Storage> {
    override fun validate(storage: Storage): List<ValidationResult> {
        return listOf(validateAnnualElectricityProduction(storage))
    }

    //hasBattery true -> should have power and capacity
    fun validateAnnualElectricityProduction(storage: Storage): ValidationResult {
        return if (storage.hasBattery == true) {
            if ((storage.batteryCapacityKwh ?: 0) == 0) {
                ValidationResult(Status.MISSING_DATA, translate("storage.batteryCapacityNotProvided"))
            } else if ((storage.batteryPowerKw ?: 0) == 0) {
                ValidationResult(Status.MISSING_DATA, translate("storage.batteryPowerNotProvided"))
            } else {
                ValidationResult(Status.VALID, translate("storage.batteryWithPowerAndCapacity"))
            }
        } else {
            ValidationResult(Status.NOT_APPLICABLE, translate("storage.withoutConnection"))
        }
    }
}
