import dev.starry.ktscheduler.executor.CoroutineExecutor
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.trigger.OneTimeTrigger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineExecutorTest {

    private lateinit var executor: CoroutineExecutor

    @Before
    fun setUp() {
        executor = CoroutineExecutor()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testExecuteSuccess(): Unit = runTest {
        val job = Job(
            jobId = "testJob1",
            function = { },
            trigger = OneTimeTrigger(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(1)),
            nextRunTime = ZonedDateTime.now(),
            dispatcher = StandardTestDispatcher(testScheduler)
        )

        val onSuccess: () -> Unit = mock(Function0::class.java) as () -> Unit
        val onError: (Throwable) -> Unit = mock(Function1::class.java) as (Throwable) -> Unit

        executor.execute(job, onSuccess, onError)
        advanceUntilIdle()

        verify(onSuccess, times(1)).invoke()
        verify(onError, never()).invoke(Throwable("Error"))
    }

    // TODO
    @Test
    fun testExecuteError(): Unit = runTest {
        val job = Job(
            jobId = "testJob2",
            function = { throw Exception("Error") },
            trigger = OneTimeTrigger(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(1)),
            nextRunTime = ZonedDateTime.now(),
            dispatcher = StandardTestDispatcher(testScheduler)
        )

        val onSuccess: () -> Unit = mock(Function0::class.java) as () -> Unit
        val onError: (Throwable) -> Unit = mock(Function1::class.java) as (Throwable) -> Unit

        executor.execute(job, onSuccess, onError)
        advanceUntilIdle()

        verify(onSuccess, never()).invoke()
        verify(onError, times(1)).invoke(Exception("Error"))
    }
}
