package dev.starry.ktscheduler

import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener
import dev.starry.ktscheduler.job.Job
import dev.starry.ktscheduler.scheduler.KtScheduler
import dev.starry.ktscheduler.trigger.IntervalTrigger
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
    val scheduler = KtScheduler()

    val defaultJob = Job(
        jobId = "1234",
        function = { println("Job executed at ${ZonedDateTime.now(timeZone)}") },
        trigger = IntervalTrigger(intervalSeconds = 15),
        nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(15),
        dispatcher = Dispatchers.Default
    )

    val eventListener = MyEventListener()
    scheduler.addEventListener(eventListener)

    scheduler.addJob(defaultJob)
    scheduler.start()

    Thread.sleep(60000)
}
