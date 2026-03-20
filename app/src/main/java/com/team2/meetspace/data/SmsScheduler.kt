package com.team2.meetspace.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class SmsScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleMeetingReminders(
        meetingTime: LocalDateTime,
        description: String,
        roomCode: String,
        phoneNumbers: List<String>
    ) {
        val now = LocalDateTime.now()

        val reminders = listOf(
            meetingTime.minusHours(1) to "Начнется через 1 час",
            meetingTime.minusMinutes(10) to "Начнется через 10 минут",
            meetingTime to "Начинается сейчас"
        )

        reminders.forEach { (triggerTime, status) ->
            val delay = Duration.between(now, triggerTime).toMillis().coerceAtLeast(0)

            if (Duration.between(now, triggerTime).toMinutes() >= -1) {
                enqueueSms(description, roomCode, phoneNumbers, status, delay, meetingTime)
            }
        }
    }

    private fun enqueueSms(
        description: String,
        roomCode: String,
        phoneNumbers: List<String>,
        status: String,
        delay: Long,
        meetingTime: LocalDateTime
    ) {
        val timeStr = meetingTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        val message = "Сегодня, $timeStr\n$description\nКод: $roomCode\n$status"

        phoneNumbers.forEach { phone ->
            val data = Data.Builder()
                .putString("phone", phone)
                .putString("message", message)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("sms_$roomCode")
                .build()

            workManager.enqueueUniqueWork(
                "sms_${roomCode}_${phone}_${status.hashCode()}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
