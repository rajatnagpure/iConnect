package com.qisystems.iconnect.tadaActivity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.data.IConnectContract
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*

class AddTada : AppCompatActivity() {

  //  private var imageView: ImageView? = null
    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null

    private var tadaDate = "N/A"
    private var editableFrom = "N/A"
    private var editableTo = "N/A"
    private var editableDistance = "N/A"
    private var editableMode = "N/A"
    private var editableExpense = "N/A"
    private var editableOtherExpense = "N/A"

    //Button Picker Things
    private var datePicker: DatePicker? = null
    private var calendar: Calendar? = null
    private var dateButton: Button? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
        // TODO Auto-generated method stub
        // arg1 = year
        // arg2 = month
        // arg3 = day
        showDate(arg1, arg2 + 1, arg3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_add_tada)

        //Button Picker things
        dateButton = findViewById<Button>(com.qisystems.iconnect.R.id.button_tada_add)
        calendar = Calendar.getInstance()
        year = calendar!!.get(Calendar.YEAR)
        month = calendar!!.get(Calendar.MONTH)
        day = calendar!!.get(Calendar.DAY_OF_MONTH)
        showDate(year, month + 1, day)

        //All the things related to edit text fiel and checking they are filled or not before proceeding further
        val meditableFrom = ((findViewById(com.qisystems.iconnect.R.id.editable_from)) as EditText)
        val meditableTo = ((findViewById(com.qisystems.iconnect.R.id.editable_to)) as EditText)
        val meditableDistance = ((findViewById(com.qisystems.iconnect.R.id.editable_distance)) as EditText)
        val meditableMode = ((findViewById(com.qisystems.iconnect.R.id.editable_mode)) as EditText)
        val meditableExpense = ((findViewById(com.qisystems.iconnect.R.id.editable_expense)) as EditText)
        val meditableOtherExpense = ((findViewById(com.qisystems.iconnect.R.id.editable_other_expense)) as EditText)
        val mImageStart = findViewById<ImageView>(com.qisystems.iconnect.R.id.image_start_reading_add_tada)
        val mImageEnd = findViewById<ImageView>(com.qisystems.iconnect.R.id.image_end_reading_add_tada)
        var checkforEditableTada = false

        if(startReadingImage != nullCheck){
            mImageStart!!.setImageBitmap(startReadingImage)
        }
        if(endReadingImage != nullCheck){
            mImageEnd!!.setImageBitmap(endReadingImage)
        }

        meditableDistance.text = SpannableStringBuilder(calculatedDistance)
        calculatedDistance = "00"

        val addTadaTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                editableFrom = meditableFrom.getText().toString()
                editableTo = meditableTo.getText().toString()
                editableDistance = meditableDistance.getText().toString()
                editableMode = meditableMode.getText().toString()
                editableExpense = meditableExpense.getText().toString()
                editableOtherExpense = meditableOtherExpense.getText().toString()
                checkforEditableTada =
                    (!editableFrom.isEmpty() && !editableTo.isEmpty() && !editableMode.isEmpty() && !editableExpense.isEmpty() && !editableOtherExpense.isEmpty())
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        // checking if all the fields are filled or not
        meditableFrom.addTextChangedListener(addTadaTextWatcher)
        meditableTo.addTextChangedListener(addTadaTextWatcher)
        meditableDistance.addTextChangedListener(addTadaTextWatcher)
        meditableMode.addTextChangedListener(addTadaTextWatcher)
        meditableExpense.addTextChangedListener(addTadaTextWatcher)
        meditableOtherExpense.addTextChangedListener(addTadaTextWatcher)

        // All about SUBMIT BUTTON
        val sumitButton = findViewById<Button>(com.qisystems.iconnect.R.id.button_submit_tada) as Button
        sumitButton.setOnClickListener {
            if (checkforEditableTada) {
                // DIALOGUE TO ALERT USER TO FILL ALL THE FIELS PROPERLY BEFORE PROCEEDING
                // Initialize a new instance of
                val builder = AlertDialog.Builder(this@AddTada)

                // Set the alert dialog title
                builder.setTitle("MESSAGE")

                // Display a message on alert dialog
                builder.setMessage("Are You Sure that All the details are good and You want to SUMIT TA/DA")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("YES") { dialog, which ->
                    Toast.makeText(applicationContext, "SUCCESSFULLY Added TA/DA!!!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, TadaList::class.java)
                    startActivity(intent)
                    /**
                     * Helper method to insert insertDealerVisit data into the database.
                     */
                    fun insertTADA() {
                        // Create a ContentValues object where column names are the keys,
                        // and Toto's pet attributes are the values.

                        val values = ContentValues ()
                        values.put(IConnectContract.TADAEntry.CURRENT_DATE, tadaDate.toString())
                        values.put(IConnectContract.TADAEntry.FROM_PLACE, editableFrom.toString())
                        values.put(IConnectContract.TADAEntry.TO_PLACE, editableTo.toString())
                        values.put(IConnectContract.TADAEntry.DISTANCE, editableDistance.toInt())
                        values.put(IConnectContract.TADAEntry.MODE, editableMode.toString())
                        values.put(IConnectContract.TADAEntry.GPS_CALCULATED_DISTANCE, distanceTrqavelledDayStartGPS)
                        values.put(IConnectContract.TADAEntry.START_IMAGE_READING, getBytes(startReadingImage!!))
                        values.put(IConnectContract.TADAEntry.END_IMAGE_READING, getBytes(endReadingImage!!))
                        values.put(IConnectContract.TADAEntry.EXPENSE, editableExpense.toInt())
                        values.put(IConnectContract.TADAEntry.OTHER_EXPENSES, editableOtherExpense.toInt())

                        uploadTadas(startReadingImage, endReadingImage, tadaDate, editableFrom, editableTo, editableDistance, editableMode, distanceTrqavelledDayStartGPS.toString(), editableExpense.toString(), editableOtherExpense.toString())

                        // Insert a new row for Toto into the provider using the ContentResolver.
                        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
                        // into the pets database table.
                        // Receive the new content URI that will allow us to access Toto's data in the future.
                        val uri = contentResolver.insert(IConnectContract.TADAEntry.CONTENT_URI, values)
                        Log.i("INSERT TADA", uri!!.toString())
                    }
                    insertTADA()
                    startReadingImage = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888)
                    endReadingImage = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888)
                    distanceTrqavelledDayStartGPS = 0.0
                    pointTravelled.clear()
                }
                // Display a neutral button on alert dialog
                builder.setNeutralButton("Cancel") { _, _ ->
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {
                // DIALOGUE TO ALERT USER TO FILL ALL THE FIELS PROPERLY
                // Initialize a new instance of
                val builder = AlertDialog.Builder(this@AddTada)

                // Set the alert dialog title
                builder.setTitle("MESSAGE")

                // Display a message on alert dialog
                builder.setMessage("Fill all the * Marked fileds Before Proceeding!!!")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OKAY") { dialog, which ->
                }
                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }
        }
    }
    private fun uploadTadas(bitmap1: Bitmap, bitmap2: Bitmap, tadaDate: String, editableFrom: String, editableTo: String, editableDistance: String, editableMode: String, distanceTrqavelledDayStartGPS: String, editableExpense: String, editableOtherExpense: String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_TADA,
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
                params["tadaDate"] = tadaDate
                params["editableFrom"] = editableFrom
                params["editableTo"] = editableTo
                params["editableDistance"] = editableDistance
                params["editableMode"] = editableMode
                params["editableTrqavelledDayStartGPS"] = distanceTrqavelledDayStartGPS
                params["editableExpense"] = editableExpense
                params["editableOtherExpense"] = editableOtherExpense
                return params
            }

            /*
             *pass files using below method
             * */
            override fun getByteData(): Map<String, DataPart>? {
                val params = HashMap<String, DataPart>()
                params["start_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap1))
                params["end_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap2))
                return params
            }
        }


        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this@AddTada)
        rQueue!!.add(volleyMultipartRequest)
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
    // convert from bitmap to byte array
    private fun getBytes(bitmap: Bitmap):ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    //Button Picker Thinhgs
    fun setTadaDate(view: View) {
        showDialog(999)
    }

    private fun showDate(year: Int, month: Int, day: Int) {
        dateButton!!.text = StringBuilder().append(day).append("/").append(month).append("/").append(year)
        tadaDate = StringBuilder().append(day).append("/").append(month).append("/").append(year).toString()
    }

    override fun onCreateDialog(id: Int): Dialog {
        // TODO Auto-generated method stub
        if (id == 999) {
            return DatePickerDialog(
                this,
                myDateListener, year, month, day
            )
        }
        return null!!
    }
}