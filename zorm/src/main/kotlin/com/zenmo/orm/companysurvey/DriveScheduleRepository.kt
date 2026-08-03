package com.zenmo.orm.companysurvey

import com.zenmo.orm.companysurvey.table.DriveScheduleTable
import com.zenmo.orm.companysurvey.table.DriveScheduleTripTable
import com.zenmo.orm.companysurvey.table.VehicleType
import com.zenmo.zummon.companysurvey.DriveSchedule
import com.zenmo.zummon.companysurvey.Trip
import kotlinx.datetime.DayOfWeek
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class DriveScheduleRepository(val db: Database) {

    fun upsertForGridConnection(gcId: UUID, vehicleType: VehicleType, schedules: List<DriveSchedule>) {
        DriveScheduleTable.batchUpsert(schedules) { schedule ->
            this[DriveScheduleTable.id] = schedule.id.toJavaUuid()
            this[DriveScheduleTable.gridConnectionId] = gcId
            this[DriveScheduleTable.vehicleType] = vehicleType
            this[DriveScheduleTable.nVehicles] = schedule.nVehicles
        }

        val scheduleIds = schedules.map { it.id.toJavaUuid() }
        DriveScheduleTripTable.deleteWhere {
            DriveScheduleTripTable.driveScheduleId.inList(scheduleIds)
        }
        DriveScheduleTripTable.batchInsert(schedules.flatMap { schedule ->
            schedule.trips.map { Pair(schedule.id.toJavaUuid(), it) }
        }) { (scheduleId, trip) ->
            this[DriveScheduleTripTable.driveScheduleId] = scheduleId
            this[DriveScheduleTripTable.dayOfWeek] = trip.dayOfWeek.name
            this[DriveScheduleTripTable.startTime] = trip.startTime
            this[DriveScheduleTripTable.endTime] = trip.endTime
        }
    }

    fun removeOrphanedSchedules(gridConnectionIds: List<UUID>, scheduleIdsToKeep: List<UUID>) {
        DriveScheduleTable.deleteWhere {
            DriveScheduleTable.gridConnectionId.inList(gridConnectionIds)
                .and(DriveScheduleTable.id.notInList(scheduleIdsToKeep))
        }
    }

    fun getByGridConnectionIds(gridConnectionIds: List<UUID>): Map<Uuid, List<Pair<VehicleType, DriveSchedule>>> {
        if (gridConnectionIds.isEmpty()) return emptyMap()

        val scheduleRows = DriveScheduleTable.selectAll()
            .where { DriveScheduleTable.gridConnectionId.inList(gridConnectionIds) }
            .toList()

        if (scheduleRows.isEmpty()) return emptyMap()

        val scheduleIds = scheduleRows.map { it[DriveScheduleTable.id] }

        val tripsByScheduleId = DriveScheduleTripTable.selectAll()
            .where { DriveScheduleTripTable.driveScheduleId.inList(scheduleIds) }
            .groupBy { it[DriveScheduleTripTable.driveScheduleId] }
            .mapValues { (_, rows) ->
                rows.map { row ->
                    Trip(
                        dayOfWeek = DayOfWeek.valueOf(row[DriveScheduleTripTable.dayOfWeek]),
                        startTime = row[DriveScheduleTripTable.startTime],
                        endTime = row[DriveScheduleTripTable.endTime],
                    )
                }
            }

        return scheduleRows
            .groupBy { it[DriveScheduleTable.gridConnectionId].toKotlinUuid() }
            .mapValues { (_, rows) ->
                rows.map { row ->
                    val vehicleType = row[DriveScheduleTable.vehicleType]
                    val schedule = DriveSchedule(
                        id = row[DriveScheduleTable.id].toKotlinUuid(),
                        nVehicles = row[DriveScheduleTable.nVehicles],
                        trips = tripsByScheduleId[row[DriveScheduleTable.id]] ?: emptyList(),
                    )
                    Pair(vehicleType, schedule)
                }
            }
    }
}