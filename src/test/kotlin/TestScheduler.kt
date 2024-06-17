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


package dev.starry.ktscheduler.test

import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.jobstore.InMemoryJobStore
import dev.starry.ktscheduler.scheduler.KtScheduler
import dev.starry.ktscheduler.triggers.OneTimeTrigger
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class KtSchedulerTest {

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
        val jobStore = InMemoryJobStore()
        val scheduler = KtScheduler(jobStore)
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

    private fun createTestJob(
        jobId: String,
        runAt: ZonedDateTime = ZonedDateTime.now().plusSeconds(1),
        raiseError: Boolean = false
    ) = Job(
        jobId = jobId,
        function = {
            if (raiseError) {
                throw RuntimeException("Error")
            }
        },
        trigger = OneTimeTrigger(runAt),
        nextRunTime = runAt,
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