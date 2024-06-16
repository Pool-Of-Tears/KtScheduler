package dev.starry.ktscheduler.trigger

import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A trigger that fires at a specific time every day.
 *
 * @param time The [LocalTime] at which the trigger should fire each day.
 */
class DailyTrigger(private val time: LocalTime) : Trigger {
    /**
     * Gets the next run time based on the current time and the specified daily time.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param timeZone The time zone in which the trigger is operating.
     * @return The next run time as a [ZonedDateTime].
     */
    override fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime {
        var nextRunTime = currentTime.withZoneSameInstant(timeZone).with(time).withNano(0)
        if (nextRunTime.isBefore(currentTime) || nextRunTime.isEqual(currentTime)) {
            nextRunTime = nextRunTime.plusDays(1)
        }
        return nextRunTime
    }
}