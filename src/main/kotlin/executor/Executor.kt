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
     * @param onSuccess The function to call when the job completes successfully.
     * @param onError The function to call when the job fails.
     */
    suspend fun execute(job: Job, onSuccess: () -> Unit, onError: (Exception) -> Unit)
}
