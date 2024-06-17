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
        val adjustedRunAt = runAt.withZoneSameInstant(currentTime.zone)
        return if (adjustedRunAt.isAfter(currentTime)) adjustedRunAt else null
    }
}