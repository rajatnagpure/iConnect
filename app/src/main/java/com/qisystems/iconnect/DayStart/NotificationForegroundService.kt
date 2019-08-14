package com.qisystems.iconnect.DayStart

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.App.Companion.CHANNEL_ID_SERVICE
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.qisystems.iconnect.*
import com.qisystems.iconnect.Constants.Companion.FASTEST_INTERVAL
import com.qisystems.iconnect.Constants.Companion.GEOFENCE_LATITUDE
import com.qisystems.iconnect.Constants.Companion.GEOFENCE_LONGITUDE
import com.qisystems.iconnect.Constants.Companion.GEOFENCE_RADIUS
import com.qisystems.iconnect.Constants.Companion.UPDATE_INTERVAL
import com.qisystems.iconnect.data.IConnectContract
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NotificationForegroundService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
    ResultCallback<Status> {


    //private var imageView: ImageView? = null
    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: java.util.ArrayList<HashMap<String, String>>? = null

    private var lastLocation: Location ?= null

    override fun onResult(status: Status) {
        Log.i("msg:", "onResult: $status")
        if ( status.isSuccess() ) {
        } else {
            // inform about fail
        } //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        startLocationUpdates()

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        if (mLocation == null) {
            startLocationUpdates()

        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show()
        }
        Log.i("Mainactivity", "Api connection successful")
        addGeofence( createGeofenceRequest( createGeofence() ) )    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient!!.connect()    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        mGoogleApiClient!!.connect()    }

    override fun onLocationChanged(location: Location?) {
        currentLatLong = location!!
        pointTravelled.add(location)
        if(pointTravelled.get(0)== placeHolderQISYS)
        {
            pointTravelled.removeAt(0)
        }

        /**
         * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
         */
        fun insertLocation() {
         //Create a ContentValues object where column names are the keys,
        //and Toto's pet attributes are the values.
        val values = ContentValues ()
        values.put(IConnectContract.LocationEntry.LATITUDE, (location!!.latitude))
        values.put(IConnectContract.LocationEntry.LONGITUDE, (location.longitude))
         //Insert a new row for Toto into the provider using the ContentResolver.
            val c = Calendar.getInstance()
            System.out.println("Current time => " + c.time)
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val time = df.format(c.time)
            uploadlocation((location!!.latitude).toString(),(location.longitude).toString(),time)

         //Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        val uri = contentResolver.insert(IConnectContract.LocationEntry.CONTENT_URI, values)
        Log.i("INSERT LOCATION", uri?.toString())
        currentLatLong = location
        }
        insertLocation()
        //val msg = "Updated Location: " +
                //location.latitude.toString() + "," +
               // location.longitude.toString()
        //Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        // You can now create a LatLng Object for use with maps
        //val latLng = LatLng(location.latitude, location.longitude)
        getAddress(location.latitude,location.longitude).toString()

        //distance calculation routines
        if(lastLocation == null){
            lastLocation = location
        }
        distanceTrqavelledDayStartGPS += GetDistanceFromLatLonInKm(
            lastLocation!!.latitude,
            lastLocation!!.longitude,
            location.latitude,
            location.longitude
        )
        lastLocation = location
    }
    private fun GetDistanceFromLatLonInKm(lat1:Double, lon1:Double, lat2:Double, lon2:Double):Double {
        val R = 6371
        // Radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)
        // deg2rad below
        val dLon = deg2rad(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val d = R * c
        // Distance in km
        return d
    }
    private fun deg2rad(deg:Double):Double {
        return deg * (Math.PI / 180)
    }
    private val GEOFENCE_REQ_ID = "My Geofence"
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var locationManager: LocationManager? = null
    private val geoFencePendingIntent: PendingIntent ?= null
    private val GEOFENCE_REQ_CODE = 0
    private var mLocationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null



    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        }


    // Create a Geofence
    private fun createGeofence( ): Geofence {
        Log.d("Creating geofenceObject", "createGeofence");
        return  Geofence.Builder()
            .setRequestId(GEOFENCE_REQ_ID)
            .setCircularRegion(GEOFENCE_LATITUDE , GEOFENCE_LONGITUDE, GEOFENCE_RADIUS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT )
            .build()
    }
    // Create a Geofence Request
    private fun createGeofenceRequest( geofence: Geofence  ): GeofencingRequest {
        Log.d("function", "createGeofenceRequest");
        return GeofencingRequest.Builder()
            .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
            .addGeofence( geofence )
            .build()
    }
    private fun createGeofencePendingIntent(): PendingIntent {
        Log.d("msg:", "createGeofencePendingIntent")
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent

        val intent = Intent(this, GeofenceTrasitionService::class.java)
        return PendingIntent.getService(
            this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    // Add the created GeofenceRequest to the device's monitoring list
    private fun addGeofence(request: GeofencingRequest) {
        Log.d("msg:", "addGeofence")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
            LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                request,
                createGeofencePendingIntent()
            ).setResultCallback(this)
    }

    override fun onCreate() {
        super.onCreate()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("gggg","uooo");
        //checkLocation()
    }
    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
            mLocationRequest, this)
        Log.d("reque", "--->>>>")
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")

        if (mGoogleApiClient != null) {
            // Call GoogleApiClient connection when starting the Activity
            mGoogleApiClient!!.connect()
        }

        val notificationIntent = Intent(this, StartDay::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
            .setContentTitle("Example Service")
            .setContentText(input)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSmallIcon(com.qisystems.iconnect.R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        //do heavy work on a background thread
        //stopSelf();

        return Service.START_NOT_STICKY
    }
    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    fun getAddress(lat:Double, lng:Double) {
        val geocoder = Geocoder(this)
        try
        {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val obj = addresses.get(0)
            var add = obj.getAddressLine(0)
            add = add + "\n" + obj.getAdminArea()
            add = add + "\n" + obj.getSubAdminArea()
            place = add + "\n" + obj.getLocality()
            Log.v("IGA", "Address" + add)
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();
            // TennisAppActivity.showDialog(add);
        }
        catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(this, "Place Error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun uploadlocation(lat: String, long: String, datetime:String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_LOCATIONS,
            Response.Listener { response ->
                Log.d("ressssssoo", String(response.data))
                rQueue!!.cache.clear()
                try {
                    val jsonObject = JSONObject(String(response.data))
                    Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true") {

                        arraylist = java.util.ArrayList()
                        val dataArray = jsonObject.getJSONArray("data")

                        var url = ""
                        for (i in 0 until dataArray.length()) {
                            val dataobj = dataArray.getJSONObject(i)
                            url = dataobj.optString("pathToFile")
                        }
                        // Picasso.get().load(url).into(imageView)
                    }
                } catch (e: JSONException) {
                    //e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = SharedPref.read(SharedPref.ID,0).toString()  //add string parameters
                params["lat"] = lat
                params["long"] = long
                params["datetime"] = datetime
                return params
            }

            /*
             *pass files using below method
             * */
            override fun getByteData(): Map<String, DataPart>? {
                val params = HashMap<String, DataPart>()
                //params["start_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap1))
                // params["end_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap2))
                return params
            }
        }


        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this@NotificationForegroundService)
        rQueue!!.add(volleyMultipartRequest)
    }
}