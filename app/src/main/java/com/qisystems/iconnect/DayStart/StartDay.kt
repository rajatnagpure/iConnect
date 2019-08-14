package com.qisystems.iconnect.DayStart

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.media.ExifInterface
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.qisystems.iconnect.*
import com.qisystems.iconnect.tadaActivity.AddTada
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class StartDay : AppCompatActivity(), OnMapReadyCallback {
    var dayStartTime: String ?= null
    var dayStartDay: String ?= null
    var mStartDayImage: ImageView ?= null
    var mEndDayImage: ImageView ?= null
    var personalUsageInKm: String ?= null

    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        }

    val filename: String
        get() {
            val file = File(Environment.getExternalStorageDirectory().path, Constants.APP_PATH_SD_CARD + Constants.TADA_IMAGES)
            if (!file.exists()) {
                file.mkdirs()
            }
            val name = System.currentTimeMillis()
            SharedPref.write(SharedPref.IMAGE_NAME, name.toString())
            return file.absolutePath + "/" + name + ".jpg"
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_day_start)

        checkLocation() //check whether location service is enable or not in your  phone
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        //Starting Geofence service and fetch location
        val serviceIntent = Intent(this, NotificationForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Geofencing has Started")
        startService(serviceIntent)


        //current date and time fetching
        if(dayStartDay == null && dayStartTime == null) {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("yyyy-MM-dd")
            dayStartDay = df.format(c.time)
            val tf = SimpleDateFormat("HH:mm")
            dayStartTime = tf.format(c.time)
        }

        mStartDayImage = findViewById(com.qisystems.iconnect.R.id.image_start_reading) as ImageView
        mEndDayImage = findViewById(com.qisystems.iconnect.R.id.image_end_reading) as ImageView

        if(startReadingImage != nullCheck){
            mStartDayImage!!.setImageBitmap(startReadingImage)
        }
        if(endReadingImage != nullCheck){
            mEndDayImage!!.setImageBitmap(endReadingImage)
        }

        mStartDayImage!!.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                dispatchTakePictureIntent(START_IMAGE_CAPTURE)
            }
        })
        mEndDayImage!!.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                dispatchTakePictureIntent(END_IMAGE_CAPTURE)
            }
        })

        val mDateText = findViewById<TextView>(com.qisystems.iconnect.R.id.text_date_start_day)
        mDateText.text = dayStartDay

        val mTimeText = findViewById<TextView>(com.qisystems.iconnect.R.id.text_time_start_day)
        mTimeText.text = dayStartTime

        val mStartReading = findViewById<EditText>(com.qisystems.iconnect.R.id.text_start_reading) as EditText
        mStartReading.text = SpannableStringBuilder(startDayReading.toString())

        val mEndReading = findViewById<EditText>(com.qisystems.iconnect.R.id.text_end_reading) as EditText
        mEndReading.text = SpannableStringBuilder(endDayReading)

        val mpersonalUsageInKm = findViewById<EditText>(com.qisystems.iconnect.R.id.text_personal_usage_day_start) as EditText


        val enterReadingTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                startDayReading = mStartReading.getText().toString()
                endDayReading = mEndReading.getText().toString()
                personalUsageInKm = mpersonalUsageInKm.getText().toString()
            }
            override fun afterTextChanged(s: Editable) {
            }
        }
        mStartReading.addTextChangedListener(enterReadingTextWatcher)
        mEndReading.addTextChangedListener(enterReadingTextWatcher)
        mpersonalUsageInKm.addTextChangedListener(enterReadingTextWatcher)

        // setting distqance travelled text.
        val textDistanceTravelled = findViewById<TextView>(com.qisystems.iconnect.R.id.Text_distance_travelled_day_start)as TextView
        textDistanceTravelled.text = roundOffDecimal(distanceTrqavelledDayStartGPS).toString()!!
        Log.i("Null", "first call "+ distanceTrqavelledDayStartGPS.toString())

        //Updating UI for map and Distance Calculation update
        val t = object : Thread() {
            override fun run() {
                while (!isInterrupted) {
                    try {
                        sleep(1000)  //1000ms = 1 sec
                        runOnUiThread {
                            textDistanceTravelled.text = distanceTrqavelledDayStartGPS.toString()!!
                            Log.i("Null", "update call"+ distanceTrqavelledDayStartGPS.toString())
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        t.start()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.qisystems.iconnect.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    //Image capture function
    val START_IMAGE_CAPTURE = 1
    val END_IMAGE_CAPTURE = 2

    private fun dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == START_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                val contentURI = data.data
                try {
                    Log.i("Image", compressImage(contentURI!!.toString()))
                    //Uri.fromFile(File(compressImage(contentURI.toString())))

                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        Uri.fromFile(File(compressImage(contentURI.toString())))
                    )
                    // imageView.setImageBitmap(bitmap);
                    mStartDayImage!!.setImageBitmap(bitmap)
                    startReadingImage = bitmap

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@StartDay, "Image Capture Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }else if(requestCode == END_IMAGE_CAPTURE && resultCode == RESULT_OK){
            if (data != null) {
                val contentURI = data.data
                try {
                    Log.i("Image", compressImage(contentURI!!.toString()))
                    //Uri.fromFile(File(compressImage(contentURI.toString())))

                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        Uri.fromFile(File(compressImage(contentURI.toString())))
                    )
                    // imageView.setImageBitmap(bitmap);
                    mEndDayImage!!.setImageBitmap(bitmap)
                    endReadingImage = bitmap

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@StartDay, "Image Capture Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.####")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var lastLocationForCamera = LatLng(21.1388038,79.077757)

        for (item in pointTravelled) {
            createMarker(item.latitude, item.longitude)
            lastLocationForCamera = LatLng(item.latitude, item.longitude)
        }
        // Add a marker in Sydney and move the camera
        //val qiSYS = LatLng(currentLatLong!!.latitude, currentLatLong!!.longitude)
        //mMap.addMarker(MarkerOptions().position(qiSYS).title("QI Systems"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationForCamera, 18f))

        //Updating UI for map and Distance Calculation update
        val t = object : Thread() {
            override fun run() {
                while (!isInterrupted) {
                    try {
                        sleep(1000)  //1000ms = 1 sec
                        runOnUiThread {
                            for (item in pointTravelled) {
                                createMarker(item.latitude, item.longitude)
                                lastLocationForCamera = LatLng(item.latitude, item.longitude)
                            }
                            // Add a marker in Sydney and move the camera
                            //val qiSYS = LatLng(currentLatLong!!.latitude, currentLatLong!!.longitude)
                            //mMap.addMarker(MarkerOptions().position(qiSYS).title("QI Systems"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationForCamera, 18f))
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        t.start()
    }

    private fun createMarker(latitude:Double, longitude:Double):Marker {
        return mMap.addMarker(MarkerOptions()
            .position(LatLng(latitude, longitude))
            .anchor(0.5f, 0.5f))
    }
    fun endDayButton(v:View){
        // DIALOGUE TO ALERT USER TO FILL ALL THE FIELS PROPERLY BEFORE PROCEEDING TO CLICK IMAGE OF CROP
        // Initialize a new instance of
        val builder = android.app.AlertDialog.Builder(this@StartDay)

        // Set the alert dialog title
        builder.setTitle("MESSAGE")

        // Display a message on alert dialog
        builder.setMessage("Re you sure you want to end yor day. You may not be able to count distance travelled if you click end day button")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes, I do"){dialog, which ->
            //Intent to stop service
            val serviceIntent = Intent(this, NotificationForegroundService::class.java)
            stopService(serviceIntent)
            dayStartTime = null
            dayStartDay = null
            calculatedDistance = (("0"+endDayReading).toInt() - ("0"+startDayReading).toInt()).toString()
            startDayReading = "00"
            endDayReading = "00"
            val intent = Intent(this, AddTada::class.java)
            startActivity(intent)
        }
        // Display a neutral button on alert dialog
        builder.setNeutralButton("No"){_,_ ->
        }

        // Finally, make the alert dialog using builder
        val dialog: android.app.AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
    fun compressImage(imageUri: String): String {

        val filePath = getRealPathFromURI(imageUri)
        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        //      max Height and width values of the compressed image is taken as 816x612

        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

        //      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        //      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        canvas.matrix = scaleMatrix
        canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        val filename = filename
        try {
            out = FileOutputStream(filename)

            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return filename

    }
    private fun getRealPathFromURI(contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }
}
