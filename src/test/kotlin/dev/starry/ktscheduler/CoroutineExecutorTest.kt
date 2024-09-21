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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineExecutorTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScheduler: TestCoroutineScheduler

    @Before
    fun setUp() {
        testScheduler = TestCoroutineScheduler()
        testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testExecuteSuccess() = runTest {
        val executor = CoroutineExecutor()
        val job = createTestJob { }
        val latch = CountDownLatch(1)
        var onSuccessCalled = false
        val onSuccess: () -> Unit = {
            onSuccessCalled = true
            latch.countDown()
        }
        val onError: (Throwable) -> Unit = { fail("onError should not be called") }

        executor.execute(job, onSuccess, onError)
        // Advance time to ensure the job is executed
        advanceUntilIdle()
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for job execution")
        assertTrue(onSuccessCalled, "onSuccess should have been called")
    }

    @Test
    fun testExecuteError() = runTest {
        val executor = CoroutineExecutor()
        val job = createTestJob { throw IllegalArgumentException("Error") }
        val latch = CountDownLatch(1)
        var exceptionCaught: Throwable? = null
        val onSuccess: () -> Unit = { fail("onSuccess should not be called") }
        val onError: (Throwable) -> Unit = {
            exceptionCaught = it
            latch.countDown()
        }

        executor.execute(job, onSuccess, onError)
        // Advance time to ensure the job is executed
        advanceUntilIdle()
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for job execution")
        assertNotNull(exceptionCaught, "Exception should have been caught")
        assertTrue(exceptionCaught is IllegalArgumentException, "Exception should be IllegalArgumentException")
        assertEquals("Error", (exceptionCaught as IllegalArgumentException).message)
    }

    @Test
    fun testConcurrentExecution() = runTest {
        val executor = CoroutineExecutor()
        val job = createTestJob(runConcurrently = true) {
            // Simulate some work
            delay(100)
        }

        val latch = CountDownLatch(3)
        var executionCount = 0
        val onSuccess: () -> Unit = {
            executionCount++
            latch.countDown()
        }
        val onError: (Throwable) -> Unit = { fail("onError should not be called") }

        // Execute the job 3 times, all should run concurrently
        repeat(3) { executor.execute(job, onSuccess, onError) }
        // Advance the virtual time to let all jobs finish
        advanceTimeBy(110)
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for job executions")
        assertEquals(3, executionCount, "All three jobs should have executed")
    }

    @Test
    fun testNonConcurrentExecution() = runTest {
        val executor = CoroutineExecutor()
        val job = createTestJob(runConcurrently = false) {
            // Simulate some work
            delay(100)
        }

        val latch = CountDownLatch(1)
        var executionCount = 0
        val onSuccess: () -> Unit = {
            executionCount++
            latch.countDown()
        }
        val onError: (Throwable) -> Unit = { fail("onError should not be called") }

        // Execute the job 3 times, but only one should actually run since it's non-concurrent
        repeat(3) { executor.execute(job, onSuccess, onError) }
        // Advance the virtual time to let the first job finish
        advanceTimeBy(110)
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for job execution")
        assertEquals(1, executionCount, "Only one job should have executed")
    }


    private fun createTestJob(
        jobId: String = "job1",
        runConcurrently: Boolean = true,
        callback: suspend () -> Unit
    ): Job = Job(
        jobId = jobId,
        trigger = OneTimeTrigger(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(1)),
        nextRunTime = ZonedDateTime.now(),
        dispatcher = testDispatcher,
        runConcurrently = runConcurrently,
        callback = callback
    )
}
