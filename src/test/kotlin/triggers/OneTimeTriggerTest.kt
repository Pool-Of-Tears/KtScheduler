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
 *  limitations under the License.
 */


package dev.starry.ktscheduler.test.triggers

import dev.starry.ktscheduler.triggers.OneTimeTrigger
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OneTimeTriggerTest {

    @Test
    fun `getNextRunTime should return run time if it is after the current time`() {
        val timeZone = ZoneId.of("Asia/Kolkata")
        val currentTime = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)

        // Test case 1: Run time is 30 minutes after the current time
        val runTime1 = ZonedDateTime.of(2023, 6, 12, 10, 30, 0, 0, timeZone)
        val trigger1 = OneTimeTrigger(runTime1)
        val expectedNextRunTime1 = runTime1
        val actualNextRunTime1 = trigger1.getNextRunTime(currentTime, timeZone)
        assertEquals(expectedNextRunTime1, actualNextRunTime1)

        // Test case 2: Run time is 1 hour after the current time
        val runTime2 = ZonedDateTime.of(2023, 6, 12, 11, 0, 0, 0, timeZone)
        val trigger2 = OneTimeTrigger(runTime2)
        val expectedNextRunTime2 = runTime2
        val actualNextRunTime2 = trigger2.getNextRunTime(currentTime, timeZone)
        assertEquals(expectedNextRunTime2, actualNextRunTime2)
    }

    @Test
    fun `getNextRunTime should return null if run time is before or equal to the current time`() {
        val timeZone = ZoneId.of("Asia/Kolkata")
        val currentTime = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)

        // Test case 1: Run time is 30 minutes before the current time
        val runTime1 = ZonedDateTime.of(2023, 6, 12, 9, 30, 0, 0, timeZone)
        val trigger1 = OneTimeTrigger(runTime1)
        val actualNextRunTime1 = trigger1.getNextRunTime(currentTime, timeZone)
        assertNull(actualNextRunTime1)

        // Test case 2: Run time is equal to the current time
        val runTime2 = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)
        val trigger2 = OneTimeTrigger(runTime2)
        val actualNextRunTime2 = trigger2.getNextRunTime(currentTime, timeZone)
        assertNull(actualNextRunTime2)
    }

    @Test
    fun `getNextRunTime should return run time in the specified time zone`() {
        val currentTime = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, ZoneId.of("Asia/Kolkata"))
        val runTime = ZonedDateTime.of(2023, 6, 12, 12, 0, 0, 0, ZoneId.of("UTC"))
        val trigger = OneTimeTrigger(runTime)

        val expectedNextRunTime = ZonedDateTime.of(2023, 6, 12, 17, 30, 0, 0, ZoneId.of("Asia/Kolkata"))
        val actualNextRunTime = trigger.getNextRunTime(currentTime, ZoneId.of("Asia/Kolkata"))
        assertEquals(expectedNextRunTime, actualNextRunTime)
    }
}