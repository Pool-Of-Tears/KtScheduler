package dev.ktscheduler.scheduler

import dev.ktscheduler.event.JobEvent
import dev.ktscheduler.event.JobEventListener
import dev.ktscheduler.event.JobStatus
import dev.ktscheduler.executor.CoroutineExecutor
import dev.ktscheduler.job.Job
import dev.ktscheduler.job.store.InMemoryJobStore
import dev.ktscheduler.job.store.JobStore
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
 * @param maxGraceTime The maximum grace time for a job to be considered due. Default is 1 minute.
 */
class KtScheduler(
    private val jobStore: JobStore = InMemoryJobStore(),
    private val timeZone: ZoneId = ZoneId.systemDefault(),
    private val maxGraceTime: Duration = Duration.ofMinutes(1)
) : Scheduler {

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

    /**
     * Starts the scheduler.
     *
     * The scheduler will run in a coroutine and continuously process due jobs
     * at the specified tick interval unless it is paused.
     *
     * @param tickInterval The tick interval in milliseconds. Default is 1000 milliseconds.
     */
    override fun start(tickInterval: Long?) {
        coroutineScope.launch {
            while (isActive) {
                if (!isPaused) {
                    processDueJobs()
                }
                delay(tickInterval ?: 1000L)
            }
        }
    }

    /**
     * Shuts down the scheduler.
     */
    override fun shutdown() {
        coroutineScope.cancel()
    }

    /**
     * Adds a job to the scheduler.
     *
     * @param job The job to add.
     */
    override fun addJob(job: Job) {
        jobStore.addJob(job)
    }

    /**
     * Removes a job from the scheduler by its ID.
     *
     * @param jobId The ID of the job to remove.
     */
    override fun removeJob(jobId: String) {
        jobStore.removeJob(jobId)
    }

    /**
     * Retrieves all jobs currently managed by the scheduler.
     *
     * @return A list of all jobs.
     */
    override fun getJobs(): List<Job> {
        return jobStore.getAllJobs()
    }

    /**
     * Pauses the scheduler.
     *
     * When paused, the scheduler will not process any due jobs.
     */
    override fun pause() {
        isPaused = true
    }

    /**
     * Resumes the scheduler.
     *
     * When resumed, the scheduler will continue processing due jobs.
     */
    override fun resume() {
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
        pausedJobs.remove(jobId)
    }

    /**
     * Adds a job event listener to the scheduler.
     *
     * @param listener The listener to add.
     */
    fun addEventListener(listener: JobEventListener) {
        eventListeners.add(listener)
    }

    // Processes due jobs and executes them.
    private suspend fun processDueJobs() {
        val now = ZonedDateTime.now(timeZone)
        val dueJobs = jobStore
            .getDueJobs(currentTime = now, maxGraceTime = maxGraceTime)
            .filterNot { pausedJobs.contains(it.jobId) }

        dueJobs.forEach { job ->
            try {
                executor.execute(job)
                handleJobCompletion(job, now)
            } catch (e: Exception) {
                notifyJobError(job.jobId, e)
            }
        }
    }

    // Handles the completion of a job by updating the next run time or removing the job.
    private fun handleJobCompletion(job: Job, now: ZonedDateTime) {
        val nextRunTime = job.trigger.getNextRunTime(now, timeZone)
        if (nextRunTime != null) {
            jobStore.updateJobNextRunTime(job.jobId, nextRunTime)
        } else {
            jobStore.removeJob(job.jobId)
        }
        notifyJobComplete(job.jobId)
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
