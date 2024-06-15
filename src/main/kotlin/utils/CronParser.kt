package dev.ktscheduler.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

/**
 * A utility class for parsing cron expressions and calculating the next run time.
 *
 * @param cronExpression The cron expression to parse.
 */
class CronParser(private val cronExpression: String) {
    private val fields: List<String> = cronExpression.split(" ")

    /**
     * Calculates the next run time based on the current time and the parsed cron expression.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param timeZone The time zone in which the trigger is operating.
     * @return The next run time as a [ZonedDateTime].
     */
    fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime {
        var nextRunTime = currentTime.withZoneSameInstant(timeZone)

        // Adjust the next run time based on each field of the cron expression
        nextRunTime = adjustField(nextRunTime, ChronoField.MINUTE_OF_HOUR, fields[0])
        nextRunTime = adjustField(nextRunTime, ChronoField.HOUR_OF_DAY, fields[1])
        nextRunTime = adjustField(nextRunTime, ChronoField.DAY_OF_MONTH, fields[2])
        nextRunTime = adjustField(nextRunTime, ChronoField.MONTH_OF_YEAR, fields[3])
        nextRunTime = adjustField(nextRunTime, ChronoField.DAY_OF_WEEK, fields[4])

        // If the adjusted next run time is before or equal to the current time,
        // increment it by the smallest unit until it's after the current time
        while (!nextRunTime.isAfter(currentTime)) {
            nextRunTime = nextRunTime.plus(1, ChronoUnit.MINUTES)
        }

        return nextRunTime
    }

    /**
     * Adjusts the given [ZonedDateTime] based on the cron field value.
     *
     * @param dateTime The [ZonedDateTime] to adjust.
     * @param field The [ChronoField] representing the field to adjust.
     * @param value The cron field value.
     * @return The adjusted [ZonedDateTime].
     */
    private fun adjustField(dateTime: ZonedDateTime, field: ChronoField, value: String): ZonedDateTime {
        return when (value) {
            "*" -> dateTime
            else -> {
                val fieldValue = value.toInt()
                dateTime.with(field, fieldValue.toLong())
            }
        }
    }
}