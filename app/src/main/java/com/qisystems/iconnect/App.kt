package com.qisystems.iconnect

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class App : Application() {
    private var appContext: Context? = null
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        createNotificationChannel()

        // INITIALISE SHARED PREFERENCE
        SharedPref.init(applicationContext)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_SERVICE,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val myChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = Color.GREEN
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(serviceChannel)
            nManager.createNotificationChannel(myChannel)
        }
    }

    companion object {
        const val CHANNEL_ID_SERVICE = "exampleServiceChannel"
        const val CHANNEL_ID = "my.app.CHANNEL_ID"
        const val CHANNEL_NAME = "Notification"
    }
}