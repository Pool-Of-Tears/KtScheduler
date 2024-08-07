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


package dev.starry.ktscheduler.test

import dev.starry.ktscheduler.executor.CoroutineExecutor
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.triggers.OneTimeTrigger
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.DefaultAsserter.fail
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineExecutorTest {

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testExecuteSuccess(): Unit = runTest {
        val executor = CoroutineExecutor()
        val job = createTestJob(scheduler = testScheduler) { }
        var onSuccessCalled = false
        val onSuccess: () -> Unit = { onSuccessCalled = true }
        val onError: (Throwable) -> Unit = { fail("onError should not be called") }

        executor.execute(job, onSuccess, onError)
        delay(50)
        assertTrue(onSuccessCalled)
    }

    @Test
    fun testExecuteError(): Unit = runTest {
        val executor = CoroutineExecutor()
        val job = createTestJob(scheduler = testScheduler) { throw IllegalArgumentException("Error") }

        val onSuccess: () -> Unit = { fail("onSuccess should not be called") }
        var exception: Throwable? = null
        val onError: (Throwable) -> Unit = { exception = it }

        executor.execute(job, onSuccess, onError)
        delay(50)
        assertNotNull(exception)
        assertTrue(exception is IllegalArgumentException)
        assertEquals("Error", exception.message)
    }

    @Test
    fun testConcurrentExecution(): Unit = runTest {
        val executor = CoroutineExecutor()
        // Create a job that takes 100ms to execute.
        val job = createTestJob(
            scheduler = testScheduler, runConcurrently = true
        ) { delay(100) }

        var onSuccessCalled = 0
        val onSuccess: () -> Unit = { onSuccessCalled += 1 }
        val onError: (Throwable) -> Unit = { fail("onError should not be called") }
        // Execute the job 3 times concurrently.
        executor.execute(job, onSuccess, onError)
        executor.execute(job, onSuccess, onError)
        executor.execute(job, onSuccess, onError)
        // Wait for the jobs to complete.
        delay(110)
        assertEquals(3, onSuccessCalled)
    }

    @Test
    fun testNonConcurrentExecution(): Unit = runTest {
        val executor = CoroutineExecutor()
        // Create a job that takes 100ms to execute.
        val job = createTestJob(
            scheduler = testScheduler, runConcurrently = false
        ) { delay(100) }

        var onSuccessCalled = 0
        val onSuccess: () -> Unit = { onSuccessCalled += 1 }
        val onError: (Throwable) -> Unit = { fail("onError should not be called") }
        // Execute the job 3 times concurrently.
        executor.execute(job, onSuccess, onError)
        executor.execute(job, onSuccess, onError)
        executor.execute(job, onSuccess, onError)
        // Wait for the jobs to complete.
        delay(110)
        assertEquals(1, onSuccessCalled)
    }

    private fun createTestJob(
        jobId: String = "job1",
        runConcurrently: Boolean = true,
        scheduler: TestCoroutineScheduler,
        callback: suspend () -> Unit,
    ): Job = Job(
        jobId = jobId,
        trigger = OneTimeTrigger(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(1)),
        nextRunTime = ZonedDateTime.now(),
        dispatcher = UnconfinedTestDispatcher(scheduler),
        runConcurrently = runConcurrently,
        callback = callback
    )
}
