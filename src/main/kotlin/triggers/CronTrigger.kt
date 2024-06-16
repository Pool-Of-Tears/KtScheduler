package dev.starry.ktscheduler.trigger

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A trigger that determines the next run time based on the specified days of the week and time.
 *
 * @param daysOfWeek The set of days of the week on which the trigger should run.
 * @param time The time of day at which the trigger should run.
 */
class CronTrigger(
    private val daysOfWeek: Set<DayOfWeek>,
    private val time: LocalTime
) : Trigger {
    override fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime? {
        var nextRunTime = currentTime.withZoneSameInstant(timeZone).with(time).withNano(0)
        if (nextRunTime.isBefore(currentTime) || nextRunTime.isEqual(currentTime)) {
            nextRunTime = nextRunTime.plusDays(1)
        }
        while (nextRunTime.dayOfWeek !in daysOfWeek) {
            nextRunTime = nextRunTime.plusDays(1)
        }
        return nextRunTime
    }
}