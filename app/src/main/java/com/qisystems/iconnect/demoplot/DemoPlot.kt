package com.qisystems.iconnect.demoplot

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.Constants.Companion.PRODUCT_1
import com.qisystems.iconnect.Constants.Companion.PRODUCT_2
import com.qisystems.iconnect.Constants.Companion.PRODUCT_3
import com.qisystems.iconnect.Constants.Companion.PRODUCT_4
import com.qisystems.iconnect.Constants.Companion.PRODUCT_5
import com.qisystems.iconnect.Constants.Companion.PRODUCT_6
import com.qisystems.iconnect.Constants.Companion.PRODUCT_7
import com.qisystems.iconnect.DataPart
import com.qisystems.iconnect.data.IConnectContract
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_demo_plot.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class DemoPlot : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null

    private var calendar: Calendar? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var imageView: ImageView ?= null
    private var editableNameField = "N/A"
    private var editableContact = "N/A"
    private var editableVillage = "N/A"
    private var editableDistrict = "N/A"
    private var editableState = "N/A"
    private var editableCrop = "N/A"
    private var sipnnerProduct = "N/A"
    private var editableDemoArea = "N/A"
    private var currentDate = "N/A"
    private var nextVisit = "N/A"
    private var checkPictureClicked = false
    private var cropImage: Bitmap ?= null


    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
        // TODO Auto-generated method stub
        // arg1 = year
        // arg2 = month
        // arg3 = day
        showDate(arg1, arg2 + 1, arg3)
    }
     private val myNextDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
         // TODO Auto-generated method stub
         // arg1 = year
         // arg2 = month
         // arg3 = day
         showNextDate(arg1, arg2 + 1, arg3)
     }

    val filename: String
        get() {
            val file = File(Environment.getExternalStorageDirectory().path, Constants.APP_PATH_SD_CARD + Constants.DEMO_PLOT_IMAGE)
            if (!file.exists()) {
                file.mkdirs()
            }
            val name = System.currentTimeMillis()
            SharedPref.write(SharedPref.IMAGE_NAME, name.toString())
            return file.absolutePath + "/" + name + ".jpg"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_demo_plot)

        calendar = Calendar.getInstance()
        year = calendar!!.get(Calendar.YEAR)

        month = calendar!!.get(Calendar.MONTH)
        day = calendar!!.get(Calendar.DAY_OF_MONTH)
        showDate(year, month + 1, day)

        //button2 enabling
        //if switch is enable
        val nextDateSwitch = findViewById<View>(com.qisystems.iconnect.R.id.next_date_switch) as Switch
        button2.isClickable = false
        nextDateSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                button2.isClickable = true
                showNextDate(year, month + 1, day)


            } else {
                // The switch is disabled
                button2.isClickable = false
                button2.text = "N/A"
                nextVisit = "N/A"
            }
        }
        // Spinner element
        val spinner = findViewById<View>(com.qisystems.iconnect.R.id.product_spinner) as Spinner

        // Spinner click listener
        spinner.onItemSelectedListener = this

        // Spinner Drop down elements
        val products = ArrayList<String>()
        products.add(PRODUCT_1)
        products.add(PRODUCT_2)
        products.add(PRODUCT_3)
        products.add(PRODUCT_4)
        products.add(PRODUCT_5)
        products.add(PRODUCT_6)
        products.add(PRODUCT_7)

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, products)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner.adapter = dataAdapter

        //Things to do with camera intent
        imageView = findViewById(com.qisystems.iconnect.R.id.imgv_photo)

        //All the things related to edit text fiel and checking they are filled or not before proceeding further

        val meditableNameField = ((findViewById(com.qisystems.iconnect.R.id.editable_name)) as EditText)
        val meditableContact = ((findViewById(com.qisystems.iconnect.R.id.editable_contact)) as EditText)
        val meditableVillage = ((findViewById(com.qisystems.iconnect.R.id.editable_village)) as EditText)
        val meditableDistrict = ((findViewById(com.qisystems.iconnect.R.id.editable_district)) as EditText)
        val meditableState = ((findViewById(com.qisystems.iconnect.R.id.editable_state)) as EditText)
        val meditableCrop = ((findViewById(com.qisystems.iconnect.R.id.editable_crop)) as EditText)
        val meditableDemoArea = ((findViewById(com.qisystems.iconnect.R.id.editable_demo_area)) as EditText)
        var checkForEditables = false

        val demoPlotTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                editableNameField = meditableNameField.getText().toString()
                editableContact = meditableContact.getText().toString()
                editableVillage = meditableVillage.getText().toString()
                editableDistrict = meditableDistrict.getText().toString()
                editableState = meditableState.getText().toString()
                editableCrop = meditableCrop.getText().toString()
                editableDemoArea = meditableDemoArea.getText().toString()
                checkForEditables = (!editableNameField.isEmpty() && !editableContact.isEmpty() && !editableNameField.isEmpty() && !editableVillage.isEmpty() && !editableDistrict.isEmpty() && !editableState.isEmpty() && !editableDemoArea.isEmpty())
            }
            override fun afterTextChanged(s: Editable) {
            }
        }
        // checking if all the fields are filled or not
        meditableNameField.addTextChangedListener(demoPlotTextWatcher)
        meditableContact.addTextChangedListener(demoPlotTextWatcher)
        meditableVillage.addTextChangedListener(demoPlotTextWatcher)
        meditableDistrict.addTextChangedListener(demoPlotTextWatcher)
        meditableState.addTextChangedListener(demoPlotTextWatcher)
        meditableCrop.addTextChangedListener(demoPlotTextWatcher)
        meditableDemoArea.addTextChangedListener(demoPlotTextWatcher)

        imageView!!.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                if(checkForEditables) {
                    dispatchTakePictureIntent()
                }else{

                    // DIALOGUE TO ALERT USER TO FILL ALL THE FIELS PROPERLY BEFORE PROCEEDING TO CLICK IMAGE OF CROP
                        // Initialize a new instance of
                        val builder = AlertDialog.Builder(this@DemoPlot)

                        // Set the alert dialog title
                        builder.setTitle("MESSAGE")

                        // Display a message on alert dialog
                        builder.setMessage("Fill All The * Marked Fields Before Proceeding to click Picture")

                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("OKAY"){dialog, which ->
                        }
                        // Display a neutral button on alert dialog
                        builder.setNeutralButton("Cancel"){_,_ ->
                        }

                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = builder.create()

                        // Display the alert dialog on app interface
                        dialog.show()
                    }
                }

        })

        // All about SUBMIT BUTTON
        val sumitButton = findViewById<Button>(com.qisystems.iconnect.R.id.button_submit_plot) as Button
        sumitButton.setOnClickListener {
            if(checkPictureClicked){
                // DIALOGUE TO ALERT USER TO FILL ALL THE FIELS PROPERLY BEFORE PROCEEDING TO CLICK IMAGE OF CROP
                // Initialize a new instance of
                val builder = AlertDialog.Builder(this@DemoPlot)

                // Set the alert dialog title
                builder.setTitle("MESSAGE")

                // Display a message on alert dialog
                builder.setMessage("Are You Sure that All the details are good and You want to SUMIT THE PLOT")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("YES"){dialog, which ->
                    Toast.makeText(applicationContext,"DemoPlot Submission SUCCESSFUL!!!",Toast.LENGTH_LONG).show()
                    imageView!!.setImageResource(com.qisystems.iconnect.R.drawable.ic_add_a_photo_black)

                    // convert from bitmap to byte array
                    fun getBytes(bitmap:Bitmap):ByteArray {
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
                        return stream.toByteArray()
                    }
                    /**
                     * Helper method to insert Demo Plot data into the database.
                     */
                    fun insertDemoPlot() {
                        // Create a ContentValues object where column names are the keys,
                        // and Toto's pet attributes are the values.
                        val values = ContentValues ()
                        values.put(IConnectContract.DemoPlotEntry.CURRENT_DATE, currentDate.toString())
                        values.put(IConnectContract.DemoPlotEntry.FARMER_NAME, editableNameField.toString())
                        values.put(IConnectContract.DemoPlotEntry.CONTACT, editableContact.toString())
                        values.put(IConnectContract.DemoPlotEntry.VILLAGE, editableVillage.toString())
                        values.put(IConnectContract.DemoPlotEntry.DISTRICT, editableDistrict.toString())
                        values.put(IConnectContract.DemoPlotEntry.STATE, editableState.toString())
                        values.put(IConnectContract.DemoPlotEntry.CROPE, editableCrop.toString())
                        values.put(IConnectContract.DemoPlotEntry.PRODUCT, sipnnerProduct.toString())
                        values.put(IConnectContract.DemoPlotEntry.DEMO_AREA, editableDemoArea.toString())
                        values.put(IConnectContract.DemoPlotEntry.NEXT_VISIT, nextVisit.toString())
                        values.put(IConnectContract.DemoPlotEntry.IMAGE, getBytes(cropImage!!))

                        uploadDemoPlots(cropImage!!, nextVisit.toString(),editableDemoArea.toString(),sipnnerProduct.toString(),editableCrop.toString(),editableState.toString(),editableDistrict.toString(),editableVillage.toString(),editableContact.toString(),editableNameField.toString(),currentDate.toString())
                        // Insert a new row for Toto into the provider using the ContentResolver.
                        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
                        // into the pets database table.
                        // Receive the new content URI that will allow us to access Toto's data in the future.
                         val uri = contentResolver.insert(IConnectContract.DemoPlotEntry.CONTENT_URI, values)
                         Log.i("INSERT DEMO_PLOT", uri.toString())
                    }
                    insertDemoPlot()
                    val intent = Intent(this, DemoPlotList::class.java)
                    startActivity(intent)
                }
                // Display a neutral button on alert dialog
                builder.setNeutralButton("Cancel"){_,_ ->
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }else{
                // DIALOGUE TO ALERT USER TO FILL ALL THE FIELS PROPERLY BEFORE PROCEEDING TO CLICK IMAGE OF CROP
                // Initialize a new instance of
                val builder = AlertDialog.Builder(this@DemoPlot)

                // Set the alert dialog title
                builder.setTitle("MESSAGE")

                // Display a message on alert dialog
                builder.setMessage("First Capture the Picture of crop before SUMITTING DEMO PLOT")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OKAY"){dialog, which ->
                }
                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }
        }

    }


    fun setDate(view: View) {
        showDialog(999)
    }
     fun setNextDate(view: View) {
         showDialog(111)
     }

    override fun onCreateDialog(id: Int): Dialog? {
        // TODO Auto-generated method stub
        return when (id) {
            999 -> DatePickerDialog(this, myDateListener, year, month, day)
            111 -> DatePickerDialog(this, myNextDateListener, year, month, day)
            else -> null
        }
    }


    private fun showDate(year: Int, month: Int, day: Int) {
        button1!!.text = StringBuilder().append(day).append("/")
            .append(month).append("/").append(year)
        currentDate = StringBuilder().append(day).append("/")
            .append(month).append("/").append(year).toString()
    }
     private fun showNextDate(year: Int, month: Int, day: Int) {
         button2!!.text = StringBuilder().append(day).append("/")
             .append(month).append("/").append(year)
         nextVisit = StringBuilder().append(day).append("/")
             .append(month).append("/").append(year).toString()
     }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // On selecting a spinner item
        sipnnerProduct = parent!!.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        sipnnerProduct = parent!!.getItemAtPosition(1).toString()
    }

     //Things we are gonna implement for the development of camera intent.

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val c = Calendar.getInstance()
            System.out.println("Current time => " + c.time)
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val time = df.format(c.time)
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
                    cropImage = ProcessingBitmap(editableNameField, place, time, bitmap)
                    imageView!!.setImageBitmap(cropImage)
                    checkPictureClicked = true

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@DemoPlot, "Image Capture Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun ProcessingBitmap(captionString:String, mplace:String, mtime: String, img: Bitmap):Bitmap {
        var bm1:Bitmap
        var newBitmap:Bitmap ?= null
        try {
            bm1 = img
            var config = bm1.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config)
            val canvas = Canvas(newBitmap)
            canvas.drawBitmap(bm1, 0.0f, 0.0f, null)

            //COLOR BCAKGROUND BLOCK PAINTING
            val paintTexting = Paint(Paint.ANTI_ALIAS_FLAG)
            paintTexting.color = Color.BLACK
            paintTexting.textSize = 20f
            paintTexting.style = Paint.Style.FILL
            canvas.drawRect(
                0F,
                3 * (bm1.getHeight().toFloat()) / 4,
                bm1.getWidth().toFloat(),
                bm1.getHeight().toFloat(),
                paintTexting
            )

            //writing name
            val paintTextName = Paint(Paint.ANTI_ALIAS_FLAG)
            paintTextName.color = Color.WHITE
            paintTextName.textSize = 40.0f
            paintTextName.style = Paint.Style.FILL
            val textRectName = Rect()
            paintTextName.getTextBounds(captionString, 0, captionString.length, textRectName)
            val xPosName = 2
            val yPosName = (3 * (bm1.getHeight()) / 4) + 44
            canvas.drawText(captionString, xPosName.toFloat(), yPosName.toFloat(), paintTextName)

            //writing Address
            val paintTextAddress = Paint(Paint.ANTI_ALIAS_FLAG)
            paintTextAddress.color = Color.WHITE
            paintTextAddress.textSize = 20.0f
            paintTextAddress.style = Paint.Style.FILL
            val textRectAdd = Rect()
            paintTextAddress.getTextBounds(mplace, 0, mplace.length, textRectAdd)
            val xPosAdd = 2
            val yPosAdd = (3 * (bm1.getHeight()) / 4) + 80
            canvas.drawText(mplace, xPosAdd.toFloat(), yPosAdd.toFloat(), paintTextAddress)

            //writing Address
            val paintTextDT = Paint(Paint.ANTI_ALIAS_FLAG)
            paintTextDT.color = Color.WHITE
            paintTextDT.textSize = 20.0f
            paintTextDT.style = Paint.Style.FILL
            val textRectDT = Rect()
            paintTextDT.getTextBounds(mtime, 0, mtime.length, textRectDT)
            val xPosDT = 2
            val yPosDT = (3 * (bm1.getHeight()) / 4) + 112
            canvas.drawText(mtime, xPosDT.toFloat(), yPosDT.toFloat(), paintTextDT)
        }
        catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return newBitmap!!
    }
    private fun uploadDemoPlots(bitmap1: Bitmap, nextVisit:String, editableDemoArea:String, sipnnerProduct:String, editableCrop:String, editableState:String, editableDistrict:String, editableVillage:String, editableContact:String, editableNameField:String, currentDate:String) {
        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_DEMOPLOTS,
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
                        //Picasso.get().load(url).into(imageView)
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
                params["nextVisit"] = nextVisit
                params["editableDemoArea"] = editableDemoArea
                params["sipnnerProduct"] = sipnnerProduct
                params["editableCrop"] = editableCrop
                params["editableState"] = editableState
                params["editableDistrict"] = editableDistrict
                params["editableVillage"] = editableVillage
                params["editableContact"] = editableContact
                params["editableNameField"] = editableNameField
                params["currentDate"] = currentDate
                return params
            }

            /*
             *pass files using below method
             * */
            override fun getByteData(): Map<String, DataPart>? {
                val params = HashMap<String, DataPart>()
                params["crop_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap1))
                //params["end_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap2))
                return params
            }
        }


        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this@DemoPlot)
        rQueue!!.add(volleyMultipartRequest)
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
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