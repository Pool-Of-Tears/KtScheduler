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

package dev.starry.ktscheduler

import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import dev.starry.ktscheduler.triggers.IntervalTrigger
import dev.starry.ktscheduler.triggers.OneTimeTrigger
import kotlinx.coroutines.Dispatchers
import java.time.ZoneId
import java.time.ZonedDateTime

class MyEventListener : JobEventListener {
    override fun onJobComplete(event: JobEvent) {
        println("Job ${event.jobId} completed successfully at ${event.timestamp}")
    }

    override fun onJobError(event: JobEvent) {
        println("Job ${event.jobId} failed with exception ${event.exception} at ${event.timestamp}")
    }
}

fun main() {
    val timeZone = ZoneId.of("Asia/Kolkata")
    val scheduler = KtScheduler(timeZone = timeZone)

    val job = Job(
        jobId = "OneTimeJob",
        function = { println("OneTime Job executed at ${ZonedDateTime.now(timeZone)}") },
        trigger = OneTimeTrigger(ZonedDateTime.now(timeZone).plusSeconds(5)),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(5),
        dispatcher = Dispatchers.Default
    )

    val errorJob = Job(
        jobId = "RaiseErrorJob",
        function = { throw Exception("Meow >~<") },
        trigger = OneTimeTrigger(ZonedDateTime.now(timeZone).plusSeconds(10)),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(10),
        dispatcher = Dispatchers.Default
    )

    val intervalJob = Job(
        jobId = "RepeatingJob",
        function = { println("Repeating job executed at ${ZonedDateTime.now(timeZone)}") },
        trigger = IntervalTrigger(intervalSeconds = 5),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(5),
        dispatcher = Dispatchers.Default
    )

    val eventListener = MyEventListener()
    scheduler.addEventListener(eventListener)

    scheduler.addJob(job)
    scheduler.addJob(errorJob)
    scheduler.addJob(intervalJob)
    scheduler.start()

    // Block the main thread and idle the scheduler.
    scheduler.idle()
}
