package dev.ktscheduler

import dev.ktscheduler.utils.CronParser
import java.time.ZoneId
import java.time.ZonedDateTime

fun main() {
    val cronParser = CronParser("0 0 1 1 *")
    println(cronParser.getNextRunTime(currentTime = ZonedDateTime.now(), timeZone = ZoneId.systemDefault()))
}