package dev.ktscheduler.trigger
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Represents a trigger that determines when a job should run.
 */
interface Trigger {

    /**
     * Gets the next run time based on the current time and the specified interval.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param timeZone The time zone in which the trigger is operating.
     * @return The next run time as a [ZonedDateTime].
     */
    fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime?
}
