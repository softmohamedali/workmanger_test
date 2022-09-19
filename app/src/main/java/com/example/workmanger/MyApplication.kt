package com.example.workmanger

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication:Application() {

    companion object{
        const val NOTIFICATION_CHANNEL_ID_DOWNLOAD="notitfication_download"
        const val NOTIFICATION_CHANNEL_NAME_DOWNLOAD="dwonload"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(
                NOTIFICATION_CHANNEL_ID_DOWNLOAD,
                NOTIFICATION_CHANNEL_NAME_DOWNLOAD,
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManger=getSystemService(NotificationManager::class.java)
            notificationManger.createNotificationChannel(notificationChannel)
        }
    }
}