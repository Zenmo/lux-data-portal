package com.zenmo.orm.companysurvey.table

import com.zenmo.orm.dbutil.PGEnum
import com.zenmo.orm.dbutil.ZenmoUUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.time

enum class VehicleType {
    TRUCK,
    VAN,
    CAR,
}

object DriveScheduleTable : ZenmoUUIDTable("drive_schedule") {
    val gridConnectionId = uuid("grid_connection_id")
        .references(GridConnectionTable.id, onDelete = ReferenceOption.CASCADE)

    val vehicleType = customEnumeration(
        "vehicle_type",
        VehicleType::class.simpleName,
        fromDb = { VehicleType.valueOf(it as String) },
        toDb = { PGEnum(VehicleType::class.simpleName!!, it) }
    )

    val nVehicles = integer("n_vehicles")
}

object DriveScheduleTripTable : ZenmoUUIDTable("drive_schedule_trip") {
    val driveScheduleId = uuid("drive_schedule_id")
        .references(DriveScheduleTable.id, onDelete = ReferenceOption.CASCADE)

    val dayOfWeek = varchar("day_of_week", 9)

    val startTime = time("start_time")

    val endTime = time("end_time")
}