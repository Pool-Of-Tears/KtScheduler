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