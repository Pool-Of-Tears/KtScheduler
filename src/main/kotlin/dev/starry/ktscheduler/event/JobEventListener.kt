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


package dev.starry.ktscheduler.event


/**
 * Interface for listening to job events.
 */
interface JobEventListener {

    /**
     * Called when a job completes successfully.
     *
     * @param event The event associated with the completed job.
     */
    fun onJobComplete(event: JobEvent)

    /**
     * Called when a job encounters an error.
     *
     * @param event The event associated with the job error.
     */
    fun onJobError(event: JobEvent)
}