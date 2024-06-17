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


package dev.starry.ktscheduler.scheduler

import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.job.Job

/**
 * An interface for a job scheduler.
 */
interface Scheduler {

    /**
     * Starts the scheduler.
     */
    fun start()

    /**
     * Shuts down the scheduler.
     */
    fun shutdown()


    /**
     * Idles the scheduler.
     *
     * use this method to block the current thread and idle until
     * the scheduler is shut down.
     */
    fun idle()

    /**
     * Adds a job to the scheduler.
     *
     * @param job The job to add.
     */
    fun addJob(job: Job)

    /**
     * Removes a job from the scheduler.
     *
     * @param jobId The ID of the job to remove.
     */
    fun removeJob(jobId: String)

    /**
     * Gets a job by its ID.
     *
     * @param jobId The ID of the job to get.
     * @return The job with the given ID, or `null` if no such job exists.
     */
    fun getJob(jobId: String): Job?

    /**
     * Gets all jobs in the scheduler.
     *
     * @return A list of all jobs in the scheduler.
     */
    fun getJobs(): List<Job>

    /**
     * Pauses the scheduler.
     */
    fun pause()

    /**
     * Resumes the scheduler.
     */
    fun resume()

    /**
     * Checks if the scheduler is paused.
     *
     * @return `true` if the scheduler is paused, `false` otherwise.
     */
    fun isPaused(): Boolean

    /**
     * Pauses a specific job in the scheduler.
     *
     * @param jobId The ID of the job to pause.
     */
    fun pauseJob(jobId: String)

    /**
     * Resumes a specific job in the scheduler.
     *
     * @param jobId The ID of the job to resume.
     */
    fun resumeJob(jobId: String)

    /**
     * Checks if a specific job is paused.
     *
     * @param jobId The ID of the job to check.
     * @return `true` if the job is paused, `false` otherwise.
     */
    fun isJobPaused(jobId: String): Boolean

    /**
     * Adds a listener for job events.
     *
     * @param listener The listener to add.
     */
    fun addEventListener(listener: JobEventListener)
}