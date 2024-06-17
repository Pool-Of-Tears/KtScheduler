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
 *  limitations under the License.
 */


package dev.starry.ktscheduler.event

import java.time.ZonedDateTime

/**
 * Represents an event that is emitted when a job is completed or an error occurs.
 *
 * @property jobId The ID of the job that triggered the event.
 * @property status The status of the job.
 * @property timestamp The time at which the event occurred.
 * @property exception The exception that occurred, if any.
 */
data class JobEvent(
    val jobId: String,
    val status: JobStatus,
    val timestamp: ZonedDateTime,
    val exception: Exception? = null
)

/**
 * Represents the status of a job.
 *
 * @property SUCCESS The job completed successfully.
 * @property ERROR An error occurred while processing the job.
 */
enum class JobStatus { SUCCESS, ERROR }

