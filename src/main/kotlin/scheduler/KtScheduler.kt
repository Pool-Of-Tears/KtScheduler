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

import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.event.JobStatus
import dev.starry.ktscheduler.executor.CoroutineExecutor
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.jobstore.InMemoryJobStore
import dev.starry.ktscheduler.jobstore.JobStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.logging.Logger

/**
 * A job scheduler that uses Kotlin coroutines.
 *
 * The KtScheduler is responsible for scheduling and executing jobs using Kotlin coroutines.
 * It utilizes a job store to manage the jobs and provides functionality to add, remove, pause,
 * and resume jobs. The scheduler operates within a specified time zone and allows setting a
 * maximum grace time for job execution.
 *
 * @param jobStore The job store to use. Default is [InMemoryJobStore].
 * @param timeZone The time zone in which the scheduler is operating. Default is the system default time zone.
 * @param maxGraceTime The maximum grace time for a job to be considered due. Default is `null` (no grace time).
 */
class KtScheduler(
    private val jobStore: JobStore = InMemoryJobStore(),
    private val timeZone: ZoneId = ZoneId.systemDefault(),
    private val maxGraceTime: Duration? = null
) : Scheduler {

    companion object {
        private const val TAG = "KtScheduler"
        private val logger = Logger.getLogger(TAG)
    }

    // The coroutine scope with a SupervisorJob to prevent cancellation of all jobs
    // if one of them fails.
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // The list of event listeners attached to the scheduler.
    private val eventListeners = mutableListOf<JobEventListener>()

    // The executor for executing jobs.
    private val executor = CoroutineExecutor()

    // The flag indicating if the scheduler is paused.
    private var isPaused = false

    // The set of paused jobs.
    private val pausedJobs = mutableSetOf<String>()

    // The tick interval in milliseconds.
    private val tickInterval = 100L

    /**
     * Starts the scheduler.
     *
     * The scheduler will run in a coroutine and continuously process due jobs
     * at the specified tick interval unless it is paused or shut down.
     */
    override fun start() {
        logger.info("Starting scheduler")
        coroutineScope.launch {
            while (isActive) {
                if (!isPaused) {
                    processDueJobs()
                }
                delay(tickInterval)
            }
        }
        logger.info("Scheduler started")
    }

    /**
     * Shuts down the scheduler.
     */
    override fun shutdown() {
        logger.info("Shutting down scheduler")
        coroutineScope.cancel()
        logger.info("Scheduler shut down")
    }

    /**
     * Idles the scheduler.
     *
     * use this method to block the current thread and idle until
     * the scheduler is shut down.
     */
    override fun idle() {
        logger.info("idling scheduler")
        while (coroutineScope.isActive) {
            Thread.sleep(1000)
        }
    }

    /**
     * Adds a job to the scheduler.
     *
     * @param job The job to add.
     */
    override fun addJob(job: Job) {
        logger.info("Adding job ${job.jobId}")
        jobStore.addJob(job)
    }

    /**
     * Removes a job from the scheduler by its ID.
     *
     * @param jobId The ID of the job to remove.
     */
    override fun removeJob(jobId: String) {
        logger.info("Removing job $jobId")
        jobStore.removeJob(jobId)
    }

    /**
     * Retrieves a job by its ID.
     *
     * @param jobId The ID of the job to retrieve.
     * @return The job with the given ID, or `null` if no such job exists.
     */
    override fun getJob(jobId: String): Job? {
        logger.info("Retrieving job $jobId")
        return jobStore.getJobById(jobId)
    }

    /**
     * Retrieves all jobs currently managed by the scheduler.
     *
     * @return A list of all jobs.
     */
    override fun getJobs(): List<Job> {
        logger.info("Retrieving all jobs")
        return jobStore.getAllJobs()
    }

    /**
     * Pauses the scheduler.
     *
     * When paused, the scheduler will not process any due jobs.
     */
    override fun pause() {
        logger.info("Pausing scheduler")
        isPaused = true
    }

    /**
     * Resumes the scheduler.
     *
     * When resumed, the scheduler will continue processing due jobs.
     */
    override fun resume() {
        logger.info("Resuming scheduler")
        isPaused = false
    }

    /**
     * Checks if the scheduler is paused.
     *
     * @return `true` if the scheduler is paused, `false` otherwise.
     */
    override fun isPaused(): Boolean {
        return isPaused
    }

    /**
     * Pauses a specific job in the scheduler.
     *
     * When paused, the job will not be processed even if it is due.
     *
     * @param jobId The ID of the job to pause.
     */
    override fun pauseJob(jobId: String) {
        logger.info("Pausing job $jobId")
        pausedJobs.add(jobId)
    }

    /**
     * Resumes a specific job in the scheduler.
     *
     * When resumed, the job will be processed if it is due.
     *
     * @param jobId The ID of the job to resume.
     */
    override fun resumeJob(jobId: String) {
        logger.info("Resuming job $jobId")
        pausedJobs.remove(jobId)
    }

    /**
     * Checks if a specific job is paused.
     *
     * @param jobId The ID of the job to check.
     * @return `true` if the job is paused, `false` otherwise.
     */
    override fun isJobPaused(jobId: String): Boolean {
        return pausedJobs.contains(jobId)
    }

    /**
     * Adds a job event listener to the scheduler.
     *
     * @param listener The listener to add.
     */
    override fun addEventListener(listener: JobEventListener) {
        eventListeners.add(listener)
    }

    // Processes due jobs and executes them.
    private suspend fun processDueJobs() {
        val now = ZonedDateTime.now(timeZone)
        val dueJobs = jobStore
            .getDueJobs(currentTime = now, maxGraceTime = maxGraceTime)
            .filterNot { pausedJobs.contains(it.jobId) }

        dueJobs.forEach { job ->
            logger.info("Processing due jobs...")
            executor.execute(
                job = job,
                onSuccess = { handleJobCompletion(job, now) },
                onError = { exc -> handleJobError(job, now, exc) }
            )
        }
    }

    // Handles the completion of a job by updating the next run time or removing the job.
    private fun handleJobCompletion(job: Job, now: ZonedDateTime) {
        setNextRunTimeOrRemoveJob(job, now)
        notifyJobComplete(job.jobId)
    }

    // Handles an error encountered while executing a job.
    private fun handleJobError(job: Job, now: ZonedDateTime, exception: Exception) {
        logger.severe("Error executing job ${job.jobId}: $exception")
        setNextRunTimeOrRemoveJob(job, now)
        notifyJobError(job.jobId, exception)
    }

    private fun setNextRunTimeOrRemoveJob(job: Job, now: ZonedDateTime) {
        val nextRunTime = job.trigger.getNextRunTime(now, timeZone)
        if (nextRunTime != null) {
            logger.info("Updating next run time for job ${job.jobId} to $nextRunTime")
            jobStore.updateJobNextRunTime(job.jobId, nextRunTime)
        } else {
            logger.info("Removing job ${job.jobId} as it has no next run time")
            jobStore.removeJob(job.jobId)
        }
    }

    // Notifies listeners that a job has completed successfully.
    private fun notifyJobComplete(jobId: String) {
        val event = JobEvent(jobId, JobStatus.SUCCESS, ZonedDateTime.now(timeZone))
        eventListeners.forEach { it.onJobComplete(event) }
    }

    // Notifies listeners that a job has encountered an error.
    private fun notifyJobError(jobId: String, exception: Exception) {
        val event = JobEvent(jobId, JobStatus.ERROR, ZonedDateTime.now(timeZone), exception)
        eventListeners.forEach { it.onJobError(event) }
    }
}
