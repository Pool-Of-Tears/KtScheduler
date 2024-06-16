package dev.starry.ktscheduler.executor

import dev.starry.ktscheduler.job.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An executor that executes jobs using coroutines.
 */
class CoroutineExecutor : Executor {

    /**
     * Executes the given job.
     *
     * @param job The job to execute.
     * @param onSuccess The function to call when the job is executed successfully.
     * @param onError The function to call when an exception occurs during job execution.
     */
    override suspend fun execute(
        job: Job, onSuccess: () -> Unit, onError: (Exception) -> Unit
    ) {
        CoroutineScope(job.dispatcher).launch {
            try {
                job.function()
                withContext(Dispatchers.Default) {
                    onSuccess()
                }
            } catch (exc: Exception) {
                withContext(Dispatchers.Default) {
                    onError(exc)
                }
            }
        }
    }
}
