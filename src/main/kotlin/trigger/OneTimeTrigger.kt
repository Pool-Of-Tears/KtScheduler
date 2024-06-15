package dev.ktscheduler.trigger

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A trigger that fires once at a specified time.
 *
 * @param runAt The [ZonedDateTime] at which the trigger should fire.
 */
class OneTimeTrigger(private val runAt: ZonedDateTime) : Trigger {
    /**
     * Gets the next run time based on the current time and the specified run time.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param timeZone The time zone in which the trigger is operating.
     * @return The next run time as a [ZonedDateTime] if it is after the current time, or `null` if the trigger has already fired.
     */
    override fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime? {
        return if (runAt.isAfter(currentTime)) runAt else null
    }
}