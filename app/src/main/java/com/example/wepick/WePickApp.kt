package com.example.wepick

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class WePickApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                PUSH_CHANNEL_ID,
                "wepick msg",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager?.createNotificationChannel(
                channel
            )
        }
    }

    companion object {
        const val PUSH_CHANNEL_ID = "wepick_push"
    }

}