package com.zenmo.vallum

import com.zenmo.zummon.companysurvey.DriveSchedule
import kotlinx.datetime.LocalTime

/**
 * Derives a 7×24 boolean matrix from a list of drive schedules.
 * Rows are days of the week (Monday=0 … Sunday=6).
 * Columns are hours of the day (0–23).
 * true = vehicle is driving (not available for charging) during that hour.
 *
 * Multiple schedules are OR-ed together: if any schedule has a trip covering an hour,
 * that hour is marked true.
 */
fun toDrivingHourMatrix(driveSchedules: List<DriveSchedule>): Array<BooleanArray> {
    val matrix = Array(7) { BooleanArray(24) }

    for (schedule in driveSchedules) {
        for (trip in schedule.trips) {
            val dayIndex = trip.dayOfWeek.value - 1 // MONDAY=1 → 0, SUNDAY=7 → 6
            for (h in trip.startTime.hour until trip.endTime.hour) {
                matrix[dayIndex][h] = true
            }
            // If the vehicle returns mid-hour (e.g. 17:30), that hour is also driving time
            if (trip.endTime > LocalTime(trip.endTime.hour, 0)) {
                matrix[dayIndex][trip.endTime.hour] = true
            }
        }
    }

    return matrix
}
