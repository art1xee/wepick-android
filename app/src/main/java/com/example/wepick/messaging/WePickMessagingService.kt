package com.example.wepick.messaging

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.wepick.R
import com.example.wepick.WePickApp
import com.example.wepick.data.repository.FirebaseUserRepository
import com.example.wepick.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WePickMessagingService() : FirebaseMessagingService() {
    private val userRepository: UserRepository = FirebaseUserRepository()
    override fun onNewToken(
        token: String,
    ) {
        Log.d("FCM_TOKEN", token)
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.updateFcmToken(token)
                .onSuccess { Log.d("FCM_TOKEN", "saved to firestore") }
                .onFailure { e -> Log.d("FCM_TOKEN", "failed to save: ${e.message}") }
        }

    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationId = System.currentTimeMillis().toInt()
        val notification = NotificationCompat.Builder(this, WePickApp.PUSH_CHANNEL_ID)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        }
    }

}