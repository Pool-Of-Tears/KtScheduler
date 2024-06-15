package dev.starry.ktscheduler.executor

import dev.starry.ktscheduler.job.Job

/**
 * An interface for executing jobs.
 */
interface Executor {

    /**
     * Executes the given job.
     *
     * @param job The job to execute.
     */
    suspend fun execute(job: Job)
}
