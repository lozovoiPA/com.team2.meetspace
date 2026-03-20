package com.team2.meetspace.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.team2.meetspace.BuildConfig

class SmsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val phoneNumber = inputData.getString("phone")
            ?: return Result.failure()

        val message = inputData.getString("message")
            ?: return Result.failure()

        // Используем BuildConfig для переключения между Mock и Real
        val smsSender: SmsSender = if (BuildConfig.DEBUG) {
            MockSmsSender(applicationContext)
        } else {
            RealSmsSender(applicationContext)
        }

        return try {
            Log.d("SmsWorker", "Sending SMS to $phoneNumber")
            smsSender.sendSms(phoneNumber, message)
            Result.success()
        } catch (e: Exception) {
            Log.e("SmsWorker", "Error sending SMS", e)
            Result.retry()
        }
    }
}
