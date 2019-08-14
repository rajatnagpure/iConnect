package com.qisystems.iconnect.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import com.qisystems.iconnect.App.Companion.CHANNEL_ID
import com.qisystems.iconnect.MainActivity
import com.qisystems.iconnect.R

class NotificationService : Service() {
    val context = this as Context
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {

    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val msg = (intent.extras.get("message") as String)

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mNotificationId: Int = 1000

        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context,
                CHANNEL_ID
            )
        } else {
            Notification.Builder(context)
        }.apply {
            setContentIntent(pendingIntent)
            setSmallIcon(R.mipmap.ic_launcher)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            setAutoCancel(true)
            setSound(defaultSoundUri)
            setContentTitle("GeoFence Alert")
            setStyle(Notification.BigTextStyle().bigText(msg))
            setContentText("Geofence Transition")
        }.build()

        nManager.notify(mNotificationId, mNotification)
        return Service.START_STICKY
    }

}