package dev.starry.ktscheduler.executor

import dev.starry.ktscheduler.exception.JobExecutionException
import dev.starry.ktscheduler.job.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An executor that executes jobs using coroutines.
 */
class CoroutineExecutor : Executor {

    /**
     * Executes the given job.
     *
     * @param job The job to execute.
     * @throws JobExecutionException If an error occurs while executing the job.
     */
    override suspend fun execute(job: Job) {
        CoroutineScope(job.dispatcher).launch {
            try {
                job.function()
            } catch (e: Exception) {
                throw JobExecutionException("Error executing job ${job.jobId}: ${e.message}", e)
            }
        }
    }
}
