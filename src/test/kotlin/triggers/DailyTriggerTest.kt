package triggers


import dev.starry.ktscheduler.trigger.DailyTrigger
import org.junit.Test
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class DailyTriggerTest {

    @Test
    fun `getNextRunTime should return next run time at the specified daily time`() {
        val dailyTime = LocalTime.of(10, 0)
        val timeZone = ZoneId.of("Asia/Kolkata")
        val trigger = DailyTrigger(dailyTime)

        // Test case 1: Current time is before the trigger time
        // =====================================================
        // 9:00 AM
        val currentTime1 = ZonedDateTime.of(2023, 6, 12, 9, 0, 0, 0, timeZone)
        // Same day 10:00 AM
        val expectedNextRunTime1 = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)
        val actualNextRunTime1 = trigger.getNextRunTime(currentTime1, timeZone)
        assertEquals(expectedNextRunTime1, actualNextRunTime1)

        // Test case 2: Current time is after the trigger time
        // ====================================================
        // 11:00 AM
        val currentTime2 = ZonedDateTime.of(2023, 6, 12, 11, 0, 0, 0, timeZone)
        // Next day 10:00 AM
        val expectedNextRunTime2 = ZonedDateTime.of(2023, 6, 13, 10, 0, 0, 0, timeZone)
        val actualNextRunTime2 = trigger.getNextRunTime(currentTime2, timeZone)
        assertEquals(expectedNextRunTime2, actualNextRunTime2)

        // Test case 3: Current time is at the exact trigger time
        // ======================================================
        // 10:00 AM
        val currentTime3 = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)
        // Next day 10:00 AM
        val expectedNextRunTime3 = ZonedDateTime.of(2023, 6, 13, 10, 0, 0, 0, timeZone)
        val actualNextRunTime3 = trigger.getNextRunTime(currentTime3, timeZone)
        assertEquals(expectedNextRunTime3, actualNextRunTime3)
    }

    @Test
    fun `getNextRunTime should handle time zone correctly`() {
        val dailyTime = LocalTime.of(10, 0)
        val timeZone = ZoneId.of("Asia/Kolkata")
        val trigger = DailyTrigger(dailyTime)

        // Test case 1: Current time is in UTC
        val currentTime1 =
            ZonedDateTime.of(2023, 6, 12, 4, 30, 0, 0, ZoneId.of("UTC")) // UTC 04:30 (10:00 AM in Asia/Kolkata)
        val expectedNextRunTime1 =
            ZonedDateTime.of(2023, 6, 13, 10, 0, 0, 0, timeZone) // Next day 10:00 AM in Asia/Kolkata
        val actualNextRunTime1 = trigger.getNextRunTime(currentTime1, timeZone)
        assertEquals(expectedNextRunTime1, actualNextRunTime1)

        // Test case 2: Current time is in Asia/Kolkata
        val currentTime2 = ZonedDateTime.of(2023, 6, 12, 11, 0, 0, 0, timeZone) // 11:00 AM in Asia/Kolkata
        val expectedNextRunTime2 =
            ZonedDateTime.of(2023, 6, 13, 10, 0, 0, 0, timeZone) // Next day 10:00 AM in Asia/Kolkata
        val actualNextRunTime2 = trigger.getNextRunTime(currentTime2, timeZone)
        assertEquals(expectedNextRunTime2, actualNextRunTime2)
    }
}