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

import dev.starry.ktscheduler.triggers.IntervalTrigger
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class IntervalTriggerTest {

    @Test
    fun `getNextRunTime should return next run time based on the specified interval`() {
        val interval = 60L // 60 seconds
        val timeZone = ZoneId.of("Asia/Kolkata")
        val trigger = IntervalTrigger(interval)

        // Test case 1: Current time is the starting point
        val currentTime1 = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)
        val expectedNextRunTime1 = ZonedDateTime.of(2023, 6, 12, 10, 1, 0, 0, timeZone)
        val actualNextRunTime1 = trigger.getNextRunTime(currentTime1, timeZone)
        assertEquals(expectedNextRunTime1, actualNextRunTime1)

        // Test case 2: Current time is in the middle of an interval
        val currentTime2 = ZonedDateTime.of(2023, 6, 12, 10, 0, 30, 0, timeZone)
        val expectedNextRunTime2 = ZonedDateTime.of(2023, 6, 12, 10, 1, 30, 0, timeZone)
        val actualNextRunTime2 = trigger.getNextRunTime(currentTime2, timeZone)
        assertEquals(expectedNextRunTime2, actualNextRunTime2)

        // Test case 3: Current time is at the end of an interval
        val currentTime3 = ZonedDateTime.of(2023, 6, 12, 10, 1, 0, 0, timeZone)
        val expectedNextRunTime3 = ZonedDateTime.of(2023, 6, 12, 10, 2, 0, 0, timeZone)
        val actualNextRunTime3 = trigger.getNextRunTime(currentTime3, timeZone)
        assertEquals(expectedNextRunTime3, actualNextRunTime3)
    }

    @Test
    fun `getNextRunTime should handle different interval values`() {
        val timeZone = ZoneId.of("Asia/Kolkata")
        val currentTime = ZonedDateTime.of(2023, 6, 12, 10, 0, 0, 0, timeZone)

        // Test case 1: Interval of 30 seconds
        val interval1 = 30L
        val trigger1 = IntervalTrigger(interval1)
        val expectedNextRunTime1 = ZonedDateTime.of(2023, 6, 12, 10, 0, 30, 0, timeZone)
        val actualNextRunTime1 = trigger1.getNextRunTime(currentTime, timeZone)
        assertEquals(expectedNextRunTime1, actualNextRunTime1)

        // Test case 2: Interval of 5 minutes
        val interval2 = 300L // 5 * 60 seconds
        val trigger2 = IntervalTrigger(interval2)
        val expectedNextRunTime2 = ZonedDateTime.of(2023, 6, 12, 10, 5, 0, 0, timeZone)
        val actualNextRunTime2 = trigger2.getNextRunTime(currentTime, timeZone)
        assertEquals(expectedNextRunTime2, actualNextRunTime2)

        // Test case 3: Interval of 1 hour
        val interval3 = 3600L // 60 * 60 seconds
        val trigger3 = IntervalTrigger(interval3)
        val expectedNextRunTime3 = ZonedDateTime.of(2023, 6, 12, 11, 0, 0, 0, timeZone)
        val actualNextRunTime3 = trigger3.getNextRunTime(currentTime, timeZone)
        assertEquals(expectedNextRunTime3, actualNextRunTime3)
    }
}