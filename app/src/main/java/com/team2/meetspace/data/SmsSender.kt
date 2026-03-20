package com.team2.meetspace.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

interface SmsSender {
    fun sendSms(phoneNumber: String, message: String)
}

class RealSmsSender(private val context: Context) : SmsSender {

    override fun sendSms(phoneNumber: String, message: String) {
        try {
            if (context.checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("SmsSender", "SEND_SMS permission not granted")
                return
            }

            val smsManager = context.getSystemService(SmsManager::class.java)
            if (smsManager == null) {
                Log.e("SmsSender", "SmsManager is null")
                return
            }

            val normalizedPhone = phoneNumber.replace("[^0-9+]".toRegex(), "")

            smsManager.sendTextMessage(
                normalizedPhone,
                null,
                message,
                null,
                null
            )

            Log.d("SmsSender", "SMS sent to $normalizedPhone")

        } catch (e: Exception) {
            Log.e("SmsSender", "Failed to send SMS to $phoneNumber", e)
        }
    }
}

class MockSmsSender(private val context: Context) : SmsSender {
    override fun sendSms(phoneNumber: String, message: String) {
        val logMessage = "MOCK SMS to $phoneNumber: $message"
        Log.d("SmsSender", logMessage)
        
        showNotification(message)
    }

    private fun showNotification(message: String) {
        val channelId = "sms_debug_channel"
        val notificationId = 1001 

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SMS Debug"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Напоминание о встрече")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            
            if (Build.VERSION.SDK_INT < 33 || 
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notificationId, builder.build())
            }
        } catch (e: Exception) {
            Log.e("SmsSender", "Could not show notification", e)
        }
    }
}
