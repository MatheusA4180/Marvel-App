package com.example.marvelapp.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.marvelapp.R

object NotificationUtils {

    private const val CHANNEL_ID = "app_channel"
    private const val CHANNEL_NAME = "Channel App"
    private const val NOTIFICATION_ID = 0

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationChannel.run {
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
            description = "Description Test"
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    @SuppressLint("MissingPermission")
    @Suppress("LongParameterList")
    fun sendNotification(
        appContext: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_ID,
        channelName: String = CHANNEL_NAME,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(channelId, channelName, notificationManager)
        }

        val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

}
