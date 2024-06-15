package dev.ktscheduler.trigger

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A trigger that fires at a specified interval.
 *
 * @param intervalSeconds The interval, in seconds, between each trigger execution.
 */
class IntervalTrigger(private val intervalSeconds: Long) : Trigger {
    /**
     * Gets the next run time based on the current time and the specified interval.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param timeZone The time zone in which the trigger is operating.
     * @return The next run time as a [ZonedDateTime].
     */
    override fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime {
        return currentTime.plusSeconds(intervalSeconds)
    }
}
