<div align="center">
  <a href="https://github.com/starry-shivam/KtScheduler">
  <img width="180" height="180" align="start" src="./assets/KtScheduler_Icon.png" alt="KtScheduler Logo">
  </a>
  <h2>KtScheduler: Kotlin Task Scheduler</h2>
</div>

<p align="center">
  <img alt="GitHub" src="https://img.shields.io/github/license/Pool-Of-Tears/Myne">
  <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/Pool-Of-Tears/KtScheduler">
  <a href="https://jitpack.io/#Pool-Of-Tears/KtScheduler"><img src="https://jitpack.io/v/Pool-Of-Tears/KtScheduler.svg" alt="Version Badge"></a>
  <img src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/starry-shivam/040646868c4c6473297b0d4e1546bf21/raw/KtScheduler_coverage_badge.json" alt="coverage badge">
  <a href="https://github.com/Pool-Of-Tears/KtScheduler/actions/workflows/tests.yml"><img src="https://github.com/Pool-Of-Tears/KtScheduler/actions/workflows/tests.yml/badge.svg" alt="tests"></a>
  <a href="https://www.repostatus.org/#active"><img src="https://www.repostatus.org/badges/latest/active.svg" alt="Project Status: Active – The project has reached a stable, usable state and is being actively developed." /></a>
</p>

**KtScheduler** is a lightweight task/job scheduling library for Kotlin, powered by Kotlin coroutines! The design of
this library is inspired by the [APScheduler](https://github.com/agronholm/apscheduler) library for Python, while
keeping things simple and easy to use.

------

### Installation 🛠️

Add the Jitpack repository in your build configuration:

```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.Pool-Of-Tears:KtScheduler:version")
}
```

------

### Documentation 📑

> [!Note]
>
> Full documentation for all classes and functions in KDoc format can be
> found [here](https://javadoc.jitpack.io/com/github/Pool-Of-Tears/KtScheduler/latest/javadoc/index.html).

Here's a quick start:

```kotlin
val timeZone = ZoneId.of("Asia/Kolkata")
val scheduler = KtScheduler(timeZone = timeZone)

// Create a job like this
val job = Job(
    jobId = "OneTimeJob", // Must be unique for each job
    trigger = OneTimeTrigger(ZonedDateTime.now(timeZone).plusSeconds(5)),
    // Next runtime of the job; when creating the job for the first time,
    // it will be used as the initial runtime.
    nextRunTime = ZonedDateTime.now(timeZone).plusSeconds(5),
    // Coroutine dispatcher in which the job should be executed.
    dispatcher = Dispatchers.Default,
    // Callback function that will be executed when the job is triggered.
    callback = { println("OneTime Job executed at ${ZonedDateTime.now(timeZone)}") }
)

// ..or like this
val job = Job(...) { println("Meow >~<") }

// Add the job to the scheduler
scheduler.addJob(job)

// Start the scheduler
scheduler.start()

// If you're running this as a standalone program, you need to block the current thread
// to prevent the program from exiting, since scheduler.start() is a non-blocking call.
scheduler.idle()
```

#### Triggers

Triggers determine when and at what frequency a particular job should be executed. KtScheduler provides four types of
triggers:

1. `CronTrigger` - A trigger that determines the next run time based on the specified days of the week and time.

```kotlin
// Days when the job should fire
val daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
val time = LocalTime.of(10, 0) // 10:00 AM (Time of the day)
val trigger = CronTrigger(daysOfWeek, time)
```

2. `DailyTrigger` - A trigger that fires at a specific time every day.

```kotlin
// Run daily at 10:00 AM
val dailyTime = LocalTime.of(10, 0)
val trigger = DailyTrigger(dailyTime)
```

3. `IntervalTrigger` - A trigger that fires at a specified interval.

```kotlin
val interval = 60L // Every 60 seconds
val trigger = IntervalTrigger(interval)
```

4. `OneTimeTrigger` - A trigger that fires once at a specified time.

```kotlin
// Run after 5 seconds
val trigger = OneTimeTrigger(ZonedDateTime.now().plusSeconds(5))
```

##### Creating Your Own Trigger

You can easily create your own custom trigger by implementing the `Trigger` interface.
Here's an example to create a `WeekendTrigger`, a trigger that should fire on weekends at a specified time.

```kotlin
import dev.starry.ktscheduler.triggers.Trigger

class WeekendTrigger(private val time: LocalTime) : Trigger {
    private val weekendDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

    override fun getNextRunTime(currentTime: ZonedDateTime, timeZone: ZoneId): ZonedDateTime {
        var nextRunTime = currentTime.withZoneSameInstant(timeZone).with(time).withNano(0)

        while (!weekendDays.contains(nextRunTime.dayOfWeek) ||
            nextRunTime.isBefore(currentTime) ||
            nextRunTime.isEqual(currentTime)
        ) {
            nextRunTime = nextRunTime.plusDays(1)
        }

        return nextRunTime
    }
}
```

#### Listening for Job Events

You can listen for job events such as completion or failure due to errors by attaching a `JobEventListener` to
the `KtScheduler`. Here's an example:

```kotlin
import dev.starry.ktscheduler.event.JobEvent
import dev.starry.ktscheduler.event.JobEventListener

scheduler.addEventListener(object : JobEventListener {
    override fun onJobComplete(event: JobEvent) {
        println("Job ${event.jobId} completed successfully at ${event.timestamp}")
    }

    override fun onJobError(event: JobEvent) {
        println("Job ${event.jobId} failed with exception ${event.exception} at ${event.timestamp}")
    }
})
```

------

### Contributing 🫶

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change, or
feel free to tackle any of the open issues present at the moment. If you're doing the latter, please leave a comment on
the issue you want to contribute to before starting to work on it.

------

### Supporting ❤️

If you found this library helpful, you can support me by giving a small tip
via [GitHub Sponsors](https://github.com/sponsors/starry-shivam) and/or joing the list
of [stargazers](https://github.com/starry-shivam/KtScheduler/stargazers) by leaving a star! 🌟

------

### License ©️

```
Copyright [2024 - Present] starry-shivam

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
