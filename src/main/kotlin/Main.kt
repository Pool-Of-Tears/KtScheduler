package dev.starry.ktscheduler

import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import dev.starry.ktscheduler.trigger.IntervalTrigger
import dev.starry.ktscheduler.trigger.OneTimeTrigger
import kotlinx.coroutines.Dispatchers
import java.time.ZoneId
import java.time.ZonedDateTime

class MyEventListener : JobEventListener {
    override fun onJobComplete(event: JobEvent) {
        println("Job ${event.jobId} completed successfully at ${event.timestamp}")
    }

    override fun onJobError(event: JobEvent) {
        println("Job ${event.jobId} failed with exception ${event.exception} at ${event.timestamp}")
    }
}

fun main() {
    val timeZone = ZoneId.of("Asia/Kolkata")
    val scheduler = KtScheduler(timeZone = timeZone)

    val job = Job(
        jobId = "OneTimeJob",
        function = { println("OneTime Job executed at ${ZonedDateTime.now(timeZone)}") },
        trigger = OneTimeTrigger(ZonedDateTime.now(timeZone).plusSeconds(5)),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(5),
        dispatcher = Dispatchers.Default
    )

    val errorJob = Job(
        jobId = "RaiseErrorJob",
        function = { throw Exception("Meow >~<") },
        trigger = OneTimeTrigger(ZonedDateTime.now(timeZone).plusSeconds(10)),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(10),
        dispatcher = Dispatchers.Default
    )

    val intervalJob = Job(
        jobId = "RepeatingJob",
        function = { println("Repeating job executed at ${ZonedDateTime.now(timeZone)}") },
        trigger = IntervalTrigger(intervalSeconds = 5),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(5),
        dispatcher = Dispatchers.Default
    )

    val eventListener = MyEventListener()
    scheduler.addEventListener(eventListener)

    scheduler.addJob(job)
    scheduler.addJob(errorJob)
    scheduler.addJob(intervalJob)
    scheduler.start()

    // Block the main thread and idle the scheduler.
    scheduler.idle()
}
