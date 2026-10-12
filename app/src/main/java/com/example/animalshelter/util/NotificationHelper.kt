package com.example.animalshelter.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.animalshelter.R

object NotificationHelper {

    const val CHANNEL_ID = "warm_paws_channel"
    const val CHANNEL_NAME = "Тёплые Лапки"
    private const val NOTIFICATION_ID = 1

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о питомцах приюта"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showHungryNotification(context: Context, hungryCount: Int) {
        val text = if (hungryCount == 1) {
            "Один питомец голоден! Покорми его 🍖"
        } else {
            "$hungryCount питомцев голодны! Покорми их 🍖"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Тёплые Лапки 🐾")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            //разрешение не выдано — игнорируем
        }
    }
}