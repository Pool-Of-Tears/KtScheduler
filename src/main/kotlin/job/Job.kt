package dev.starry.ktscheduler.job

import dev.starry.ktscheduler.trigger.Trigger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.ZonedDateTime

/**
 * Represents a job that can be scheduled to run at a specific time.
 *
 * @property jobId A unique identifier for the job.
 * @property function The function to run when the job is triggered.
 * @property trigger The trigger that determines when the job should run.
 * @property nextRunTime The next time the job should run.
 * @property dispatcher The dispatcher to run the job on.
 */
data class Job(

    /**
     * A unique identifier for the job.
     */
    val jobId: String,

    /**
     * The function to run when the job is triggered.
     */
    val function: suspend () -> Unit,

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
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
)

