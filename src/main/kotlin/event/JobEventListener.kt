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

/**
 * JobEventListener is an interface that is used to listen to the job events.
 */
interface JobEventListener {
    /**
     * This method is called when the job is completed.
     *
     * @param event The event that is triggered when the job is completed.
     */
    fun onJobComplete(event: JobEvent)

    /**
     * This method is called when there is an error while executing the job.
     *
     * @param event The event that is triggered when the job is started.
     */
    fun onJobError(event: JobEvent)
}