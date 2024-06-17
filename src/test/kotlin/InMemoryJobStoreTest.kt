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

import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.jobstore.InMemoryJobStore
import dev.starry.ktscheduler.triggers.OneTimeTrigger
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryJobStoreTest {

    private lateinit var jobStore: InMemoryJobStore

    @BeforeTest
    fun setUp() {
        jobStore = InMemoryJobStore()
    }

    @Test
    fun `addJob and getJobById should add and retrieve job correctly`() {
        val job = createTestJob("job1")
        jobStore.addJob(job)

        val retrievedJob = jobStore.getJobById("job1")
        assertEquals(job, retrievedJob)
    }

    @Test
    fun `removeJob should remove job from store`() {
        val job = createTestJob("job1")
        jobStore.addJob(job)
        jobStore.removeJob("job1")

        val retrievedJob = jobStore.getJobById("job1")
        assertNull(retrievedJob)
    }

    @Test
    fun `getDueJobs should return due jobs based on current time and grace time`() {
        val pastJob = createTestJob("pastJob", ZonedDateTime.now().minusMinutes(10))
        val futureJob = createTestJob("futureJob", ZonedDateTime.now().plusMinutes(10))
        val dueJob = createTestJob("dueJob", ZonedDateTime.now().minusSeconds(30))

        jobStore.addJob(pastJob)
        jobStore.addJob(futureJob)
        jobStore.addJob(dueJob)

        val currentTime = ZonedDateTime.now()
        val maxGraceTime = Duration.ofMinutes(1)

        // Test with maxGraceTime
        val dueJobsWithGraceTime = jobStore.getDueJobs(currentTime, maxGraceTime)
        assertEquals(1, dueJobsWithGraceTime.size)
        assertTrue(dueJobsWithGraceTime.contains(dueJob))

        // Test with smaller maxGraceTime
        val smallGraceTime = Duration.ofSeconds(10)
        val dueJobsWithSmallerGraceTime = jobStore.getDueJobs(currentTime, smallGraceTime)
        assertEquals(0, dueJobsWithSmallerGraceTime.size)

        // Test without maxGraceTime
        val dueJobsWithoutGraceTime = jobStore.getDueJobs(currentTime, null)
        assertEquals(2, dueJobsWithoutGraceTime.size)
        assertTrue(dueJobsWithoutGraceTime.contains(pastJob))
        assertTrue(dueJobsWithoutGraceTime.contains(dueJob))
    }

    @Test
    fun `updateJobNextRunTime should update job's next run time`() {
        val job = createTestJob("job1")
        jobStore.addJob(job)

        val newNextRunTime = ZonedDateTime.now().plusMinutes(10)
        jobStore.updateJobNextRunTime("job1", newNextRunTime)

        val updatedJob = jobStore.getJobById("job1")
        assertEquals(newNextRunTime, updatedJob?.nextRunTime)
    }

    @Test
    fun `getAllJobs should return all jobs in the store`() {
        val job1 = createTestJob("job1")
        val job2 = createTestJob("job2")

        jobStore.addJob(job1)
        jobStore.addJob(job2)

        val allJobs = jobStore.getAllJobs()
        assertEquals(2, allJobs.size)
        assertTrue(allJobs.contains(job1))
        assertTrue(allJobs.contains(job2))
    }

    private fun createTestJob(jobId: String, nextRunTime: ZonedDateTime = ZonedDateTime.now()): Job {
        return Job(
            jobId = jobId,
            function = {/* do nothing */ },
            trigger = OneTimeTrigger(nextRunTime),
            nextRunTime = nextRunTime,
            dispatcher = Dispatchers.Default
        )
    }
}