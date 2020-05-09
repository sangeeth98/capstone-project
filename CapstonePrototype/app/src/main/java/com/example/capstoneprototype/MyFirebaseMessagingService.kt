package com.example.capstoneprototype

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data != null) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = data["title"]
        val content = data["content"]?.split(",")
        val msg= content?.get(content.size-1)
        val notificationManger =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelID = "FCM"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                    NotificationChannel(channelID, "FCM CHECK", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "Checking the notification"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationManger.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
        notificationBuilder.apply {
            setContentTitle("Emergency Message")
            setSmallIcon(R.drawable.ic_launcher_background)
            setAutoCancel(true)
            setDefaults(Notification.DEFAULT_ALL)
            setWhen(System.currentTimeMillis())
            setContentText(title)
            setContentInfo(msg)
        }
        notificationManger.notify(Random.nextInt(), notificationBuilder.build())
    }
}