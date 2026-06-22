package com.timenw.milkteatracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "milktea_tracker_channel"
    private const val CHANNEL_NAME = "奶茶提醒"
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply { description = "奶了么提醒" }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
    fun sendSugarLimitNotification(context: Context, currentGrams: Int, targetGrams: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🧋 糖分摄入超标")
            .setContentText("今日已摄入 ${currentGrams}g 糖分，超过目标 ${targetGrams}g，建议少喝奶茶")
            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).build()
        manager.notify(7001, notification)
    }
}
