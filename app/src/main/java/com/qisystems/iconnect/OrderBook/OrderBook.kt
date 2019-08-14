package com.qisystems.iconnect.OrderBook

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.Constants.Companion.AGENCY_1
import com.qisystems.iconnect.Constants.Companion.AGENCY_2
import com.qisystems.iconnect.Constants.Companion.AGENCY_3
import com.qisystems.iconnect.Constants.Companion.AGENCY_4
import com.qisystems.iconnect.Constants.Companion.AGENCY_5
import com.qisystems.iconnect.Constants.Companion.AGENCY_6
import com.qisystems.iconnect.Constants.Companion.AGENCY_7
import com.qisystems.iconnect.Constants.Companion.AGENCY_8
import com.qisystems.iconnect.DataPart
import com.qisystems.iconnect.data.IConnectContract
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

import java.util.ArrayList
import java.util.HashMap

class OrderBook : AppCompatActivity() {

    //private var imageView: ImageView? = null
    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null

    private val CASH_MODE = 1
    private val CREDIT_MODE = 0
    private var editableDemoArea = "N/A"
    private var editableNameField = "N/A"
    private var editableContact = "N/A"
    private var editableVillage = "N/A"
    private var editableDistrict = "N/A"
    private var editableState = "N/A"
    //private var editableCrop = "N/A"
    private var paymentmode = CASH_MODE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_order_book)


        // Spinner Items Add as a Agency Name

        val sp_agename = findViewById(com.qisystems.iconnect.R.id.sp_age_name) as Spinner

        val AgencyList = ArrayList<String>()
        AgencyList.add(AGENCY_1)
        AgencyList.add(AGENCY_2)
        AgencyList.add(AGENCY_3)
        AgencyList.add(AGENCY_4)
        AgencyList.add(AGENCY_5)
        AgencyList.add(AGENCY_6)
        AgencyList.add(AGENCY_7)
        AgencyList.add(AGENCY_8)

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, AgencyList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_agename.adapter = arrayAdapter
        sp_agename.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("WrongViewCast")
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // val AgeName = parent.getItemAtPosition(position).toString()
                // Toast.makeText(parent.context, "Selected: $AgeName", Toast.LENGTH_LONG).show()

                val meditableNameField = ((findViewById(com.qisystems.iconnect.R.id.prod_name)) as EditText)
                val meditableMrp = ((findViewById(com.qisystems.iconnect.R.id.mrp)) as EditText)
                val meditableQuantity = ((findViewById(com.qisystems.iconnect.R.id.qty)) as EditText)
                val meditableRate = ((findViewById(com.qisystems.iconnect.R.id.rate)) as EditText)
                val meditableSub = ((findViewById(com.qisystems.iconnect.R.id.sub_tot)) as TextView)
                val btn_submit = ((findViewById(com.qisystems.iconnect.R.id.save_book)) as Button)


                val demoPlotTextWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        editableDemoArea = parent.getItemAtPosition(position).toString()
                        editableNameField = meditableNameField.getText().toString()
                        editableContact = meditableMrp.getText().toString()
                        editableVillage = meditableQuantity.getText().toString()
                        editableDistrict = meditableRate.getText().toString()
                        editableState = meditableSub.getText().toString()


                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                }

                meditableNameField.addTextChangedListener(demoPlotTextWatcher)
                meditableMrp.addTextChangedListener(demoPlotTextWatcher)
                meditableQuantity.addTextChangedListener(demoPlotTextWatcher)
                meditableRate.addTextChangedListener(demoPlotTextWatcher)
                meditableSub.addTextChangedListener(demoPlotTextWatcher)


            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        // Capture button clicks

        val btn_show_order = findViewById(com.qisystems.iconnect.R.id.btnshow_book) as Button
        val btn_submit = findViewById(com.qisystems.iconnect.R.id.save_book) as Button


        btn_show_order.setOnClickListener {
            // Start NewActivity.class
            val myIntent = Intent(
                this,
                OrderListView::class.java
            )
            startActivity(myIntent)
            Log.i("Main Activity", "Starting new activity")
        }

        fun onRadioButtonClicked(view: View) {
            if (view is RadioButton) {
                // Is the button now checked?
                val checked = view.isChecked

                // Check which radio button was clicked
                when (view.getId()) {
                    com.qisystems.iconnect.R.id.radio_credit ->
                        if (checked) {
                            paymentmode = CASH_MODE
                        }
                    com.qisystems.iconnect.R.id.radio_cash ->
                        if (checked) {
                            paymentmode = CREDIT_MODE
                        }
                }
            }
        }
    }
    fun onSaveButtonClicked(view: View) {

        Toast.makeText(this, "Order Save !", Toast.LENGTH_SHORT).show()

        fun insertOrder() {
            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            val values = ContentValues()
            values.put(IConnectContract.OrderbookEntry.AGENCY_NAME, editableDemoArea.toString())
            values.put(IConnectContract.OrderbookEntry.PRODUCT_NAME, editableNameField.toString())
            values.put(IConnectContract.OrderbookEntry.MRP, editableContact.toString())
            values.put(IConnectContract.OrderbookEntry.QUANTITY, editableVillage.toString())
            values.put(IConnectContract.OrderbookEntry.RATE, editableDistrict.toString())
            values.put(IConnectContract.OrderbookEntry.SUBTOTAL, editableState.toString())
            values.put(IConnectContract.OrderbookEntry.PAYMENT, paymentmode.toString())

            uploadOrderBook(editableDemoArea, editableNameField, editableContact, editableVillage, editableDistrict, editableState, paymentmode.toString())


            // Insert a new row for Toto into the provider using the ContentResolver.
            // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
            // into the pets database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            val uri = contentResolver.insert(IConnectContract.OrderbookEntry.CONTENT_URI, values)
            Log.i("INSERT ORDERBOOK", uri.toString())
        }
        insertOrder()
    }
    private fun uploadOrderBook(agencyname: String, productName: String, mrp: String, quantity: String, rate: String, subtotal: String, paymentmode: String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_ORDER_BOOK,
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
                params["agencyname"] = agencyname
                params["productName"] = productName
                params["mrp"] = mrp
                params["quantity"] = quantity
                params["rate"] = rate
                params["subtotal"] = subtotal
                params["paymentmode"] = paymentmode
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
        rQueue = Volley.newRequestQueue(this@OrderBook)
        rQueue!!.add(volleyMultipartRequest)
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}






