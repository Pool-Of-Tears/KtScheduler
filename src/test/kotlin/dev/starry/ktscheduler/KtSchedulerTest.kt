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


package dev.starry.ktscheduler.test

import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import dev.starry.ktscheduler.triggers.CronTrigger
import dev.starry.ktscheduler.triggers.DailyTrigger
import dev.starry.ktscheduler.triggers.IntervalTrigger
import dev.starry.ktscheduler.triggers.OneTimeTrigger
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class KtSchedulerTest {

    @Test
    fun `scheduler should throw exception when starting if already running`() {
        val scheduler = KtScheduler()
        scheduler.start()
        try {
            scheduler.start()
            fail("Should throw IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("Scheduler is already running", e.message)
            assertTrue(scheduler.isRunning())
        }
        scheduler.shutdown()
    }

    @Test
    fun `scheduler should throw exception when shutting down if not running`() {
        val scheduler = KtScheduler()
        try {
            scheduler.shutdown()
            fail("Should throw IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("Scheduler is not running", e.message)
            assertFalse(scheduler.isRunning())
        }
    }

    @Test
    fun `scheduler should block main thread when idle`() {
        var shutdown = false
        val thread = Thread {
            val scheduler = KtScheduler()
            scheduler.start()
            assertTrue(scheduler.isRunning())
            scheduler.idle()
            // Will reach here after interruption
            shutdown = !scheduler.isRunning()
        }
        thread.start()
        Thread.sleep(500)
        assertFalse(shutdown) // Scheduler should be running
        // Interrupt the thread
        thread.interrupt()
        thread.join()
        assertTrue(shutdown) // Scheduler should be shutdown
    }

    @Test
    fun `addJob should add job to the scheduler`() {
        val scheduler = KtScheduler()
        val job = createTestJob("job1")
        scheduler.addJob(job)

        val retrievedJob = scheduler.getJob("job1")
        assertEquals(job, retrievedJob)
    }

    @Test
    fun `removeJob should remove job from the scheduler`() {
        val scheduler = KtScheduler()
        val job = createTestJob("job1")
        scheduler.addJob(job)
        scheduler.removeJob("job1")

        val retrievedJob = scheduler.getJob("job1")
        assertNull(retrievedJob)
    }

    @Test
    fun `getJobs should return all jobs in the scheduler`() {
        val scheduler = KtScheduler()
        val job1 = createTestJob("job1")
        val job2 = createTestJob("job2")
        scheduler.addJob(job1)
        scheduler.addJob(job2)

        val jobs = scheduler.getJobs()
        assertEquals(2, jobs.size)
        assertTrue(jobs.contains(job1))
        assertTrue(jobs.contains(job2))
    }

    @Test
    fun `pause and resume should control scheduler execution`() {
        val scheduler = KtScheduler()
        scheduler.start()

        scheduler.pause()
        assertTrue(scheduler.isPaused())

        scheduler.resume()
        assertFalse(scheduler.isPaused())

        scheduler.shutdown()
    }

    @Test
    fun `scheduler should not process jobs when paused`() {
        val scheduler = KtScheduler()
        val job = createTestJob("job1")
        val eventListener = TestJobEventListener()

        scheduler.addEventListener(eventListener)
        scheduler.addJob(job)

        // Start and pause scheduler
        scheduler.start()
        scheduler.pause()
        Thread.sleep(1200)

        // Job should not be completed
        assertEquals(0, eventListener.completedJobs.size)
        // job should not be removed as it is not completed.
        val retrievedJob = scheduler.getJob("job1")
        assertNotNull(retrievedJob)
        assertEquals(job, retrievedJob)

        // Resume scheduler
        scheduler.resume()
        Thread.sleep(1200)

        // Job should be completed
        assertEquals(1, eventListener.completedJobs.size)
        assertEquals("job1", eventListener.completedJobs[0])

        // One time trigger job should be removed after execution
        val processedJob = scheduler.getJob("job1")
        assertNull(processedJob)

        scheduler.shutdown()
    }

    @Test
    fun `scheduler should not process jobs when shutdown`() {
        val scheduler = KtScheduler()
        val job = createTestJob("job1")
        val eventListener = TestJobEventListener()

        scheduler.addEventListener(eventListener)
        scheduler.addJob(job)

        // Start and shutdown scheduler
        scheduler.start()
        scheduler.shutdown()

        // Job should not be completed
        assertEquals(0, eventListener.completedJobs.size)
        // job should not be removed as it is not completed.
        val retrievedJob = scheduler.getJob("job1")
        assertNotNull(retrievedJob)
        assertEquals(job, retrievedJob)

        // Start scheduler again
        scheduler.start()
        Thread.sleep(1200)

        // Job should be completed
        assertEquals(1, eventListener.completedJobs.size)
        assertEquals("job1", eventListener.completedJobs[0])

        // One time trigger job should be removed after execution
        val processedJob = scheduler.getJob("job1")
        assertNull(processedJob)
        scheduler.shutdown()
    }

    @Test
    fun `pauseJob and resumeJob should control individual job execution`() {
        val scheduler = KtScheduler()
        val job = createTestJob("job1")
        scheduler.addJob(job)

        scheduler.pauseJob("job1")
        assertTrue(scheduler.isJobPaused("job1"))

        scheduler.resumeJob("job1")
        assertFalse(scheduler.isJobPaused("job1"))
    }

    @Test
    fun `scheduler should process due jobs`(): Unit = runTest {
        val scheduler = KtScheduler()
        val startTime = ZonedDateTime.now()
        // Job 1 should run after 1 second
        val job = createTestJob("job1", startTime.plusSeconds(1))
        // Job 2 should run after 1 second and raise error
        val errorJob = createTestJob("job2", startTime.plusSeconds(1), raiseError = true)

        scheduler.addJob(job)
        scheduler.addJob(errorJob)

        val eventListener = TestJobEventListener()
        scheduler.addEventListener(eventListener)

        scheduler.start()
        Thread.sleep(1200)

        // Job 1 should be completed
        assertEquals(1, eventListener.completedJobs.size)
        assertEquals("job1", eventListener.completedJobs[0])

        // Job 2 should raise error
        assertEquals(1, eventListener.errorJobs.size)
        assertEquals("job2", eventListener.errorJobs[0])

        // One time trigger job should be removed after execution
        val processedJob = scheduler.getJob("job1")
        val processedErrorJob = scheduler.getJob("job2")
        assertNull(processedJob)
        assertNull(processedErrorJob)
        assertEquals(0, scheduler.getJobs().size)

        scheduler.shutdown()
    }

    @Test
    fun `scheduler should reschedule jobs with recurring triggers`(): Unit = runTest {
        val scheduler = KtScheduler()
        val startTime = ZonedDateTime.now()
        // Job 1 should run after 1 second and then every 1 second
        val job = Job(
            jobId = "job1",
            trigger = IntervalTrigger(intervalSeconds = 1),
            nextRunTime = startTime.plusSeconds(1),
            callback = { /* Do nothing */ }
        )

        scheduler.addJob(job)

        val eventListener = TestJobEventListener()
        scheduler.addEventListener(eventListener)

        scheduler.start()
        Thread.sleep(2200)

        // Job 1 should be completed twice
        assertEquals(2, eventListener.completedJobs.size)
        assertEquals("job1", eventListener.completedJobs[0])
        assertEquals("job1", eventListener.completedJobs[1])

        // Job 1 should be rescheduled
        val rescheduledJob = scheduler.getJob("job1")
        assertNotNull(rescheduledJob)
        assertEquals(startTime.plusSeconds(3).year, rescheduledJob.nextRunTime!!.year)
        assertEquals(startTime.plusSeconds(3).month, rescheduledJob.nextRunTime!!.month)
        assertEquals(startTime.plusSeconds(3).dayOfMonth, rescheduledJob.nextRunTime!!.dayOfMonth)
        assertEquals(startTime.plusSeconds(3).hour, rescheduledJob.nextRunTime!!.hour)
        assertEquals(startTime.plusSeconds(3).minute, rescheduledJob.nextRunTime!!.minute)
        assertEquals(startTime.plusSeconds(3).second, rescheduledJob.nextRunTime!!.second)

        scheduler.shutdown()
    }

    @Test
    fun `scheduler should not execute job multiple times if it is still running`(): Unit = runTest {
        val scheduler = KtScheduler()

        // Create a job that takes 2 seconds to execute
        val job = Job(
            jobId = "longRunningJob",
            trigger = IntervalTrigger(intervalSeconds = 1),
            nextRunTime = ZonedDateTime.now().plusSeconds(1),
            callback = { delay(2000) }
        )
        val eventListener = TestJobEventListener()

        scheduler.addJob(job)
        scheduler.addEventListener(eventListener)
        scheduler.start()
        Thread.sleep(200)
        // Job should not be completed yet
        assertEquals(0, eventListener.completedJobs.size)
        // Wait for enough time to ensure job has run
        Thread.sleep(3000)
        scheduler.shutdown()
        // Assert that the job was only executed once
        assertEquals(1, eventListener.completedJobs.size)
        assertEquals("longRunningJob", eventListener.completedJobs[0])
    }

    @Test
    fun `scheduler should not execute job concurrently if runConcurrently is false`(): Unit = runTest {
        val scheduler = KtScheduler()

        // Create a job that takes 2 seconds to execute
        val job = Job(
            jobId = "longRunningJob",
            trigger = IntervalTrigger(intervalSeconds = 1),
            nextRunTime = ZonedDateTime.now(),
            callback = { delay(2000) },
            runConcurrently = false
        )
        val eventListener = TestJobEventListener()

        scheduler.addJob(job)
        scheduler.addEventListener(eventListener)
        scheduler.start()
        // Job should not be completed yet
        assertEquals(0, eventListener.completedJobs.size)
        // Wait for 3 seconds
        Thread.sleep(3000)
        // Assert that the job was only executed once in 3 seconds
        // because the job is not run concurrently, and it takes 2 seconds to execute
        assertEquals(1, eventListener.completedJobs.size)
        // Assert that the job was executed twice after 4 seconds
        Thread.sleep(1200)
        assertEquals(2, eventListener.completedJobs.size)
        assertEquals("longRunningJob", eventListener.completedJobs[0])
        assertEquals("longRunningJob", eventListener.completedJobs[1])
        scheduler.shutdown()
    }

    @Test
    fun `scheduler should execute job concurrently if runConcurrently is true`(): Unit = runTest {
        val scheduler = KtScheduler()

        // Create a job that takes 2 seconds to execute
        val job = Job(
            jobId = "longRunningJob",
            trigger = IntervalTrigger(intervalSeconds = 1),
            nextRunTime = ZonedDateTime.now(),
            callback = { delay(2000) },
            runConcurrently = true
        )
        val eventListener = TestJobEventListener()

        scheduler.addJob(job)
        scheduler.addEventListener(eventListener)
        scheduler.start()
        // Job should not be completed yet
        assertEquals(0, eventListener.completedJobs.size)
        // Wait for 3 seconds and shutdown the scheduler
        Thread.sleep(3100)
        scheduler.shutdown()
        // Assert that the job was executed twice in 3 seconds
        // because the job is run concurrently, and it takes 2 seconds to execute
        assertEquals(2, eventListener.completedJobs.size)
        assertEquals("longRunningJob", eventListener.completedJobs[0])
        assertEquals("longRunningJob", eventListener.completedJobs[1])
    }

    @Test
    fun `test nextRuntime is getting calculated if not passed`() {
        val scheduler = KtScheduler()
        val intervalJob = Job(
            jobId = "intervalJob",
            trigger = IntervalTrigger(intervalSeconds = 1),
            callback = { /* Do nothing */ }
        )
        val oneTimeJob = Job(
            jobId = "oneTimeJob",
            trigger = OneTimeTrigger(runAt = ZonedDateTime.now().plusSeconds(1)),
            callback = { /* Do nothing */ }
        )
        val dailyJob = Job(
            jobId = "dailyJob",
            trigger = DailyTrigger(time = LocalTime.of(10, 0)),
            callback = { /* Do nothing */ }
        )
        val cronJob = Job(
            jobId = "cronJob",
            trigger = CronTrigger(
                daysOfWeek = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                time = LocalTime.of(10, 0)
            ),
            callback = { /* Do nothing */ }
        )

        scheduler.addJob(intervalJob)
        scheduler.addJob(oneTimeJob)
        scheduler.addJob(dailyJob)
        scheduler.addJob(cronJob)

        scheduler.getJob("intervalJob")?.let {
            assertNotNull(it.nextRunTime)
            assertTrue(it.nextRunTime!! > ZonedDateTime.now())
            assertTrue(it.nextRunTime!! < ZonedDateTime.now().plusSeconds(2))
        } ?: fail("Job not found")

        scheduler.getJob("oneTimeJob")?.let {
            assertNotNull(it.nextRunTime)
            assertTrue(it.nextRunTime!! > ZonedDateTime.now())
            assertTrue(it.nextRunTime!! < ZonedDateTime.now().plusSeconds(2))
        } ?: fail("Job not found")

        scheduler.getJob("dailyJob")?.let {
            assertNotNull(it.nextRunTime)
            assertTrue(it.nextRunTime!!.hour == 10)
            assertTrue(it.nextRunTime!!.minute == 0)
        } ?: fail("Job not found")
        scheduler.getJob("cronJob")?.let {
            assertNotNull(it.nextRunTime)
            assertTrue(it.nextRunTime!!.dayOfWeek == DayOfWeek.SATURDAY || it.nextRunTime!!.dayOfWeek == DayOfWeek.SUNDAY)
            assertTrue(it.nextRunTime!!.hour == 10)
            assertTrue(it.nextRunTime!!.minute == 0)
        } ?: fail("Job not found")
    }

    @Test
    fun `test runCron schedules the cron job`() {
        val scheduler = KtScheduler()
        val jobId = scheduler.runCron(
            daysOfWeek = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
            time = LocalTime.of(10, 0)
        ) { /* do nothing */ }

        scheduler.getJob(jobId)?.let {
            assertTrue(it.trigger is CronTrigger)
            assertTrue(it.jobId.startsWith("runCron"))
            assertEquals(it.jobId, jobId)
        } ?: fail("Job not found")
    }

    @Test
    fun `test runDaily schedules the daily job`() {
        val scheduler = KtScheduler()
        val jobId = scheduler.runDaily(dailyTime = LocalTime.of(10, 0)) {/* do nothing */ }
        scheduler.getJob(jobId)?.let {
            assertTrue(it.trigger is DailyTrigger)
            assertTrue(it.jobId.startsWith("runDaily"))
            assertEquals(it.jobId, jobId)
        } ?: fail("Job not found")
    }

    @Test
    fun `test runRepeating schedules the repeating job`() {
        val scheduler = KtScheduler()
        val jobId = scheduler.runRepeating(intervalSeconds = 10) {/* do nothing */ }
        scheduler.getJob(jobId)?.let {
            assertTrue(it.trigger is IntervalTrigger)
            assertTrue(it.jobId.startsWith("runRepeating"))
            assertEquals(it.jobId, jobId)
        } ?: fail("Job not found")
    }

    @Test
    fun `test runOnce schedules the one time job`() {
        val scheduler = KtScheduler()
        val jobId = scheduler.runOnce(runAt = ZonedDateTime.now().plusSeconds(10)) {/* do nothing */ }
        scheduler.getJob(jobId)?.let {
            assertTrue(it.trigger is OneTimeTrigger)
            assertTrue(it.jobId.startsWith("runOnce"))
            assertEquals(it.jobId, jobId)
        } ?: fail("Job not found")
    }

    private fun createTestJob(
        jobId: String,
        runAt: ZonedDateTime = ZonedDateTime.now().plusSeconds(1),
        raiseError: Boolean = false
    ) = Job(
        jobId = jobId,
        trigger = OneTimeTrigger(runAt),
        nextRunTime = runAt,
        callback = {
            if (raiseError) {
                throw RuntimeException("Error")
            }
        }
    )


    private class TestJobEventListener : JobEventListener {
        val completedJobs = mutableListOf<String>()
        val errorJobs = mutableListOf<String>()

        override fun onJobComplete(event: JobEvent) {
            completedJobs.add(event.jobId)
        }

        override fun onJobError(event: JobEvent) {
            errorJobs.add(event.jobId)
        }
    }
}