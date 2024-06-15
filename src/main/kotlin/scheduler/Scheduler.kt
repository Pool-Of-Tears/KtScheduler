package dev.starry.ktscheduler.scheduler

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
}