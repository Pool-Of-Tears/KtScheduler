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


package dev.starry.ktscheduler.jobstore

import dev.starry.ktscheduler.job.Job
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * An in-memory implementation of [JobStore].
 */
class InMemoryJobStore : JobStore {

    // A map of job IDs to jobs
    private val jobs = ConcurrentHashMap<String, Job>()

    /**
     * Adds a job to the store.
     *
     * @param job The job to add.
     */
    override fun addJob(job: Job) {
        jobs[job.jobId] = job
    }

    /**
     * Removes a job from the store.
     *
     * @param jobId The ID of the job to remove.
     */
    override fun removeJob(jobId: String) {
        jobs.remove(jobId)
    }


    /**
     * Gets the due jobs based on the current time.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param maxGraceTime The maximum grace time for a job to be considered due, null if no grace time.
     */
    override fun getDueJobs(currentTime: ZonedDateTime, maxGraceTime: Duration?): List<Job> {
        return jobs.values.filter { job ->
            val jobNextRunTime = job.nextRunTime!!
            maxGraceTime?.let { graceTime ->
                jobNextRunTime <= currentTime && currentTime <= jobNextRunTime.plus(graceTime)
            } ?: (jobNextRunTime <= currentTime)
        }
    }


    /**
     * Updates the next run time of a job.
     *
     * @param jobId The ID of the job to update.
     * @param nextRunTime The new next run time.
     */
    override fun updateJobNextRunTime(jobId: String, nextRunTime: ZonedDateTime) {
        jobs[jobId]?.let { jobs[jobId] = it.copy(nextRunTime = nextRunTime) }
    }

    /**
     * Gets a job by its ID.
     *
     * @param jobId The ID of the job to get.
     * @return The job with the given ID, or null if no job exists with that ID.
     */
    override fun getJobById(jobId: String): Job? {
        return jobs[jobId]
    }

    /**
     * Gets all jobs in the store.
     *
     * @return A list of all jobs.
     */
    override fun getAllJobs(): List<Job> {
        return jobs.values.toList()
    }
}
