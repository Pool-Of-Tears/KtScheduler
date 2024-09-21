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

package dev.starry.ktscheduler.executor

import dev.starry.ktscheduler.job.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * An executor that executes jobs using coroutines.
 */
class CoroutineExecutor : Executor {

    // A map of currently running jobs.
    private val runningJobs = ConcurrentHashMap<String, Job>()
    private val lock = Any() // Lock to synchronize access to runningJobs.

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
        synchronized(lock) {
            // If the job is not allowed to run concurrently and another job
            // with the same ID is running, return.
            if (!job.runConcurrently && runningJobs.containsKey(job.jobId)) {
                return
            }
            runningJobs[job.jobId] = job
        }

        CoroutineScope(job.dispatcher).launch {
            try {
                job.callback()
                withContext(Dispatchers.Default) { onSuccess() }
            } catch (exc: Exception) {
                withContext(Dispatchers.Default) { onError(exc) }
            } finally {
                // Remove the job from the runningJobs map after execution.
                synchronized(lock) { runningJobs.remove(job.jobId) }
            }
        }
    }
}