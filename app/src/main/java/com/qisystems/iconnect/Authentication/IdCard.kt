package com.qisystems.iconnect.Authentication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.qisystems.iconnect.*
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.ArrayList
import java.util.HashMap
import com.qisystems.iconnect.Constants.Companion.UPLOAD_PROFILE_IMAGE
import com.qisystems.iconnect.DataPart

class IdCard : Activity() {

    private var imageView: ImageView? = null
    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null

    val filename: String
        get() {
            val file = File(Environment.getExternalStorageDirectory().path, Constants.APP_PATH_SD_CARD + Constants.APP_THUMBNAIL_PATH_SD_CARD)
            if (!file.exists()) {
                file.mkdirs()
            }
            val name = System.currentTimeMillis()
            SharedPref.write(SharedPref.IMAGE_NAME, name.toString())
            return file.absolutePath + "/" + name + ".jpg"

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_id_card)

        requestMultiplePermissions()
        // Locate the button in activity_main.xml
        val button = findViewById<View>(com.qisystems.iconnect.R.id.button1) as Button
        val name = findViewById<TextView>(com.qisystems.iconnect.R.id.lbl_name) as TextView
        val email = findViewById<TextView>(com.qisystems.iconnect.R.id.lbl_email) as TextView
        val designation = findViewById<TextView>(com.qisystems.iconnect.R.id.lbl_desg) as TextView
        val mobile = findViewById<TextView>(com.qisystems.iconnect.R.id.lbl_phone) as TextView

        name.text = SharedPref.read(SharedPref.NAME,"Employee Name")
        email.text = SharedPref.read(SharedPref.EMAIL,"N/A")
        designation.text = SharedPref.read(SharedPref.DESIGNATION,"N/A")
        mobile.text = SharedPref.read(SharedPref.MOBILE,"N/A")

        // Capture button clicks
        button.setOnClickListener {
            // Start NewActivity.class
            val myIntent = Intent(
                this@IdCard,
                MainActivity::class.java
            )
            startActivity(myIntent)
            Log.i("Main Activity", "Starting new activity")
        }

        imageView = findViewById(com.qisystems.iconnect.R.id.employee_img_id) as ImageView

        var thubnail:Bitmap?=null
        thubnail =getThumbnail("employee_"+ SharedPref.read(SharedPref.IMAGE_NAME,"0").toString()+".png")
        if(thubnail!=null){
            imageView!!.setImageBitmap(thubnail)}

        imageView!!.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(galleryIntent, GALLERY)
        }

    }
    fun getThumbnail(filename:String): Bitmap? {
        val fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.APP_PATH_SD_CARD + Constants.APP_THUMBNAIL_PATH_SD_CARD
        var thumbnail: Bitmap?= null
        // Look for the file on the external storage
        try
        {
            if (isSdReadable())
            {
                thumbnail = BitmapFactory.decodeFile("$fullPath/$filename")
            }
        }
        catch (e:Exception) {
            Log.e("getThumbnail()", e.message)
            return null
        }
        // If no file on external storage, look in internal storage
        if (thumbnail == null)
        {
            try
            {
                val filePath = this.getFileStreamPath(filename)
                val fi = FileInputStream(filePath)
                thumbnail = BitmapFactory.decodeStream(fi)
            }
            catch (ex:Exception) {
                Log.e("getThumbnail()", ex.message)
                return null
            }
        }
        return thumbnail!!
    }
    fun isSdReadable():Boolean {
        var mExternalStorageAvailable = false
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // We can read and write the media
            mExternalStorageAvailable = true
            Log.i("isSdReadable", "External storage card is readable.")
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            // We can only read the media
            Log.i("isSdReadable", "External storage card is readable.")
            mExternalStorageAvailable = true
        }
        else
        {
            // Something else is wrong. It may be one of many other
            // states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false
        }
        return mExternalStorageAvailable
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
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
                    uploadImage(bitmap)
                    saveImageToExternalStorage(bitmap)
                    imageView!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@IdCard, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun saveImageToExternalStorage(image:Bitmap):Boolean {
        val fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.APP_PATH_SD_CARD + Constants.APP_THUMBNAIL_PATH_SD_CARD
        try
        {
            val dir = File(fullPath)
            if (!dir.exists())
            {
                dir.mkdirs()
            }
            var fOut: OutputStream ?= null
            val file = File(fullPath, "employee_"+ SharedPref.read(SharedPref.IMAGE_NAME,"0").toString()+".png")
            file.createNewFile()
            fOut = FileOutputStream(file)
            // 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            MediaStore.Images.Media.insertImage(this.contentResolver, file.getAbsolutePath(), file.getName(), file.getName())
            return true
        }
        catch (e:Exception) {
            Log.e("saveToExternalStorage()", e.message)
            return false
        }
    }

    private fun uploadImage(bitmap: Bitmap) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(Request.Method.POST, UPLOAD_PROFILE_IMAGE,
            Response.Listener { response ->
                Log.d("ressssssoo", String(response.data))
                rQueue!!.cache.clear()
                try {
                    val jsonObject = JSONObject(String(response.data))
                    Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true") {

                        arraylist = ArrayList()
                        val dataArray = jsonObject.getJSONArray("data")

                        var url = ""
                        for (i in 0 until dataArray.length()) {
                            val dataobj = dataArray.getJSONObject(i)
                            url = dataobj.optString("pathToFile")
                        }
                        Picasso.get().load(url).into(imageView)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
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
                return params
            }

            /*
             *pass files using below method
             * */
            override fun getByteData(): Map<String, DataPart>? {
                val params = HashMap<String, DataPart>()
                params["emp_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap))
                return params
            }
        }


        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this@IdCard)
        rQueue!!.add(volleyMultipartRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this)
            .withPermissions(

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        //Toast.makeText(applicationContext, "All permissions are granted by user!", Toast.LENGTH_SHORT)
                         //   .show()
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show() }
            .onSameThread()
            .check()
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
