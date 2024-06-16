package triggers

import dev.starry.ktscheduler.trigger.CronTrigger
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class CronTriggerTest {

    @Test
    fun `getNextRunTime should return next run time on the specified days of week and time`() {
        val daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val time = LocalTime.of(10, 0)
        val timeZone = ZoneId.of("UTC")
        val trigger = CronTrigger(daysOfWeek, time)

        // Test case 1: Current time is before the trigger time on a trigger day
        // =====================================================================
        // Monday 9:00 AM
        val currentTime1 = ZonedDateTime.of(2023, 6, 12, 9, 0, 0, 0, timeZone)
        // Monday 10:00 AM
        val expectedNextRunTime1 = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)
        val actualNextRunTime1 = trigger.getNextRunTime(currentTime1, timeZone)
        assertEquals(expectedNextRunTime1, actualNextRunTime1)

        // Test case 2: Current time is after the trigger time on a trigger day
        // ====================================================================
        // Monday 11:00 AM
        val currentTime2 = ZonedDateTime.of(2023, 6, 12, 11, 0, 0, 0, timeZone)
        // Wednesday 10:00 AM
        val expectedNextRunTime2 = ZonedDateTime.of(2023, 6, 14, 10, 0, 0, 0, timeZone)
        val actualNextRunTime2 = trigger.getNextRunTime(currentTime2, timeZone)
        assertEquals(expectedNextRunTime2, actualNextRunTime2)

        // Test case 3: Current time is on a non-trigger day
        // ================================================
        // Tuesday 9:00 AM
        val currentTime3 = ZonedDateTime.of(2023, 6, 13, 9, 0, 0, 0, timeZone)
        // Wednesday 10:00 AM
        val expectedNextRunTime3 = ZonedDateTime.of(2023, 6, 14, 10, 0, 0, 0, timeZone)
        val actualNextRunTime3 = trigger.getNextRunTime(currentTime3, timeZone)
        assertEquals(expectedNextRunTime3, actualNextRunTime3)

        // Test case 4: Current time is at the exact trigger time on a trigger day
        // =======================================================================
        // Wednesday 10:00 AM
        val currentTime4 = ZonedDateTime.of(2023, 6, 14, 10, 0, 0, 0, timeZone)
        // Friday 10:00 AM
        val expectedNextRunTime4 = ZonedDateTime.of(2023, 6, 16, 10, 0, 0, 0, timeZone)
        val actualNextRunTime4 = trigger.getNextRunTime(currentTime4, timeZone)
        assertEquals(expectedNextRunTime4, actualNextRunTime4)
    }
}