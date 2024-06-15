/**
 * MIT License
 *
 * Copyright (c) [2024 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

