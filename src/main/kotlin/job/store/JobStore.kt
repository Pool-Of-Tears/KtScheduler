package dev.starry.ktscheduler.job.store

import dev.starry.ktscheduler.job.Job
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Represents a store for jobs.
 */
interface JobStore {

    /**
     * Adds a job to the store.
     *
     * @param job The job to add.
     */
    fun addJob(job: Job)

    /**
     * Removes a job from the store.
     *
     * @param jobId The ID of the job to remove.
     */
    fun removeJob(jobId: String)

    /**
     * Gets the due jobs based on the current time.
     *
     * @param currentTime The current time as a [ZonedDateTime].
     * @param maxGraceTime The maximum grace time for a job to be considered due.
     */
    fun getDueJobs(currentTime: ZonedDateTime, maxGraceTime: Duration): List<Job>

    /**
     * Updates the next run time of a job.
     *
     * @param jobId The ID of the job to update.
     * @param nextRunTime The new next run time.
     */
    fun updateJobNextRunTime(jobId: String, nextRunTime: ZonedDateTime)

    /**
     * Gets a job by its ID.
     *
     * @param jobId The ID of the job to get.
     * @return The job with the given ID, or null if no such job exists.
     */
    fun getJobById(jobId: String): Job?

    /**
     * Gets all jobs in the store.
     *
     * @return A list of all jobs.
     */
    fun getAllJobs(): List<Job>
}
