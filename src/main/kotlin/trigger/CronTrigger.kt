package dev.ktscheduler.trigger

import dev.ktscheduler.utils.CronParser
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A trigger that fires based on a cron expression.
 *
 * @param cronExpression The cron expression defining the trigger schedule.
 */
class CronTrigger(private val cronExpression: String) : Trigger {
    private val cronParser = CronParser(cronExpression)

    /**
     * Gets the next run time based on the current time and the cron expression.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param timeZone The time zone in which the trigger is operating.
     * @return The next run time as a [ZonedDateTime].
     */
    override fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime {
        return cronParser.getNextRunTime(currentTime, timeZone)
    }
}