package com.qisystems.iconnect.DayStart

import com.google.android.gms.location.GeofenceStatusCodes
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import android.app.IntentService

import android.util.Log
import com.qisystems.iconnect.notification.NotificationService


class GeofenceTrasitionService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        // Retrieve the Geofencing intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Handling errors
        if (geofencingEvent.hasError()) {
            val errorMsg =
                getErrorString(geofencingEvent.errorCode)
            Log.e(TAG, errorMsg)
            return
        }
        val context = this.applicationContext

        // Retrieve GeofenceTrasition
        val geoFenceTransition = geofencingEvent.geofenceTransition
        // Check if the transition type
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i("msg", "Entered")
            // Creating Notification
            val notificationService = Intent(context, NotificationService::class.java)
            notificationService.putExtra("message", "My Dearest Brother Please You are already Inside The defined geofence or working area")
            context.startService(notificationService)
        } else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            Log.i("msg", "Exited")
            // Creating Notification
            val notificationService = Intent(context, NotificationService::class.java)
            notificationService.putExtra("message", "My Dearest brother Please get Inside Your Alooted working area or else get killed by your boss :)")
            context.startService(notificationService)
        }
    }

    companion object {

        private val TAG = GeofenceTrasitionService::class.java.simpleName
        val GEOFENCE_NOTIFICATION_ID = 0

        // Handle errors
        private fun getErrorString(errorCode: Int): String {
            when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GeoFence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "Too many GeoFences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "Too many pending intents"
                else -> return "Unknown error."
            }
        }
    }


}