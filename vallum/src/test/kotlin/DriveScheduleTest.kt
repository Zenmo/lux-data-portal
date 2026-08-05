package com.zenmo.vallum

import com.zenmo.zummon.companysurvey.DriveSchedule
import com.zenmo.zummon.companysurvey.Trip
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DriveScheduleTest {

    @Test
    fun testSingleTripMarksCorrectDayAndHours() {
        // Monday 08:00–17:00
        val schedules = listOf(
            DriveSchedule(
                nVehicles = 1,
                trips = listOf(Trip(DayOfWeek.MONDAY, LocalTime(8, 0), LocalTime(17, 0))),
            )
        )

        val matrix = toDrivingHourMatrix(schedules)

        // Monday = index 0
        assertFalse(matrix[0][7], "Hour 7 should be false (vehicle not yet gone)")
        assertTrue(matrix[0][8], "Hour 8 should be true")
        assertTrue(matrix[0][16], "Hour 16 should be true")
        assertFalse(matrix[0][17], "Hour 17 should be false (vehicle back at exactly 17:00)")

        // Other days untouched
        assertFalse(matrix[1][8], "Tuesday hour 8 should be false")
    }

    @Test
    fun testMidHourArrivalMarksPartialHourAsDriving() {
        // Monday 08:00–17:30: hour 17 should be marked because vehicle returns at 17:30
        val schedules = listOf(
            DriveSchedule(
                nVehicles = 1,
                trips = listOf(Trip(DayOfWeek.MONDAY, LocalTime(8, 0), LocalTime(17, 30))),
            )
        )

        val matrix = toDrivingHourMatrix(schedules)

        assertTrue(matrix[0][17], "Hour 17 should be true when vehicle returns at 17:30")
        assertFalse(matrix[0][18], "Hour 18 should be false")
    }

    @Test
    fun testMultipleSchedulesAreORedTogether() {
        // Schedule A: Monday 06:00–10:00
        // Schedule B: Monday 14:00–18:00
        val schedules = listOf(
            DriveSchedule(
                nVehicles = 2,
                trips = listOf(Trip(DayOfWeek.MONDAY, LocalTime(6, 0), LocalTime(10, 0))),
            ),
            DriveSchedule(
                nVehicles = 3,
                trips = listOf(Trip(DayOfWeek.MONDAY, LocalTime(14, 0), LocalTime(18, 0))),
            ),
        )

        val matrix = toDrivingHourMatrix(schedules)

        assertTrue(matrix[0][6])
        assertTrue(matrix[0][9])
        assertFalse(matrix[0][10])
        assertFalse(matrix[0][13])
        assertTrue(matrix[0][14])
        assertTrue(matrix[0][17])
        assertFalse(matrix[0][18])
    }
}
