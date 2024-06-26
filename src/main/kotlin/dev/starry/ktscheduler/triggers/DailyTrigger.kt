/**
 * Copyright [2024 - Present] starry-shivam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package dev.starry.ktscheduler.triggers

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