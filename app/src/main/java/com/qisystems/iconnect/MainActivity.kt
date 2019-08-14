package com.qisystems.iconnect

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat.requestPermissions
import android.view.View
import com.qisystems.iconnect.tadaActivity.TadaList
import com.qisystems.iconnect.data.IConnectDbHelper
import com.qisystems.iconnect.DayStart.StartDay
import com.qisystems.iconnect.Feedback.Feedback
import com.qisystems.iconnect.OrderBook.OrderBook
import com.qisystems.iconnect.ProductCatlog.Productcatlog
import com.qisystems.iconnect.scheme.Scheme
import com.qisystems.iconnect.dealervisit.DealerVisit
import com.qisystems.iconnect.demoplot.DemoPlot
import com.qisystems.iconnect.distributor.Distributor
import com.qisystems.iconnect.todolist.Todolist
import android.graphics.BitmapFactory
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import com.qisystems.iconnect.demoplot.DemoPlotList

val nullCheck:Bitmap = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888)
val placeHolderQISYS: Location = Location("QiSyatems").apply { latitude = 21.1388038
    longitude = 79.077757}
var place: String = "N/A"
var currentLatLong:Location = placeHolderQISYS
var distanceTrqavelledDayStartGPS: Double = 0.0
var pointTravelled: ArrayList<Location>  = arrayListOf(placeHolderQISYS)
var startDayReading:String = "00"
var endDayReading:String = "00"
var startReadingImage: Bitmap = nullCheck
var endReadingImage: Bitmap = nullCheck
var calculatedDistance:String = "00"

class MainActivity : AppCompatActivity() {
    /** Database helper that will provide us access to the database  */
    private var mDbHelper: IConnectDbHelper? = null
    private var mLocationManager: LocationManager? = null
    private var locationManager: LocationManager? = null

    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_dashboard_activity)

        //startReadingImage= BitmapFactory.decodeResource(applicationContext.getResources(),com.qisystems.iconnect.R.drawable.ic_add_a_photo_black)
        //endReadingImage= BitmapFactory.decodeResource(applicationContext.getResources(),com.qisystems.iconnect.R.drawable.ic_add_a_photo_black)

        requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            111
        )
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("gggg","uooo");
        checkLocation() //check whether location service is enable or not in your  phone
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper =  IConnectDbHelper(this)
        //adding geofence

    }
    fun demoPlotOpen(view: View){
        val intent = Intent(this, DemoPlotList::class.java)
        startActivity(intent)
    }

    fun dealerVisitOpen(view: View){
        val intent = Intent(this, DealerVisit::class.java)
        startActivity(intent)
    }

    fun tadaOpen(view: View){
        val intent = Intent(this, TadaList::class.java)
        startActivity(intent)
    }

    fun dayStartOpen(view: View){
        val intent = Intent(this, StartDay::class.java)
        startActivity(intent)
    }

    fun orderBookOpen(view: View){
        val intent = Intent(this, OrderBook::class.java)
        startActivity(intent)

    }

    fun todolistOpen(view: View){
        val intent = Intent(this, Todolist::class.java)
        startActivity(intent)
    }
    fun distributorOpen(view: View){
        val intent = Intent(this, Distributor::class.java)
        startActivity(intent)
    }
    fun schemeopen(view: View){
        val intent = Intent(this, Scheme::class.java)
        startActivity(intent)
    }
    fun catlogOpen(view: View){
        val intent = Intent(this, Productcatlog::class.java)
        startActivity(intent)
    }
    fun feedbackOpen(view: View){
        val intent = Intent(this, Feedback::class.java)
        startActivity(intent)
    }


    private fun checkLocation(): Boolean {
        if (!isLocationEnabled)
            showAlert()
        return isLocationEnabled
    }
    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

}