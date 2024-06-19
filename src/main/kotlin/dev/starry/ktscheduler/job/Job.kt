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


package dev.starry.ktscheduler.job

import dev.starry.ktscheduler.triggers.Trigger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.ZonedDateTime

/**
 * Represents a job that can be scheduled to run at a specific time.
 *
 * @property jobId A unique identifier for the job.
 * @property trigger The trigger that determines when the job should run.
 * @property nextRunTime The next time the job should run.
 * @property dispatcher The dispatcher to run the job on.
 * @property callback The callback function to run when the job is triggered.
 */
data class Job(

    /**
     * A unique identifier for the job.
     */
    val jobId: String,

    /**
     * The trigger that determines when the job should run.
     */
    val trigger: Trigger,

    /**
     * The next time the job should run.
     */
    val nextRunTime: ZonedDateTime,

    /**
     * The dispatcher to run the job on.
     */
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,

    /**
     * The callback function to run when the job is triggered.
     */
    val callback: suspend () -> Unit
)

