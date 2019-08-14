package com.qisystems.iconnect.dealervisit

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.Constants.Companion.DEALER_1
import com.qisystems.iconnect.Constants.Companion.DEALER_2
import com.qisystems.iconnect.Constants.Companion.DEALER_3
import com.qisystems.iconnect.Constants.Companion.DEALER_4
import com.qisystems.iconnect.Constants.Companion.DEALER_5
import com.qisystems.iconnect.Constants.Companion.DEALER_6
import com.qisystems.iconnect.Constants.Companion.DEALER_7
import com.qisystems.iconnect.Constants.Companion.DEALER_8
import com.qisystems.iconnect.DataPart
import com.qisystems.iconnect.data.IConnectContract
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DealerVisit : AppCompatActivity(), OnItemSelectedListener {
    //private var imageView: ImageView? = null
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_dealer_visit)

        // Spinner element
        val spinner = findViewById<View>(com.qisystems.iconnect.R.id.dealers_spinner) as Spinner

        // Spinner click listener
        spinner.onItemSelectedListener = this

        // Spinner Drop down elements
        val dealer = ArrayList<String>()
        dealer.add(DEALER_1)
        dealer.add(DEALER_2)
        dealer.add(DEALER_3)
        dealer.add(DEALER_4)
        dealer.add(DEALER_5)
        dealer.add(DEALER_6)
        dealer.add(DEALER_7)
        dealer.add(DEALER_8)
        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dealer)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner.adapter = dataAdapter

    }

    var item : String?= null
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        // On selecting a spinner item
        item = parent.getItemAtPosition(position).toString()

        //buttons used to select and mark the selected item
        val mark_visited = findViewById<Button>(com.qisystems.iconnect.R.id.mark_visited)
        mark_visited.setOnClickListener {
            Toast.makeText(parent.context, "Selected: $item and Marked as visited", Toast.LENGTH_LONG).show()
            /**
             * Helper method to insert insertDealerVisit data into the database.
             */
            fun insertDealerVisit() {
                // Create a ContentValues object where column names are the keys,
                // and Toto's pet attributes are the values.
                val c = Calendar.getInstance()
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val time = df.format(c.time)

                val values = ContentValues ()
                values.put(IConnectContract.DealerVisitEntry.LATITUDE, (currentLatLong!!.latitude).toDouble())
                values.put(IConnectContract.DealerVisitEntry.LONGITUDE, (currentLatLong!!.longitude).toDouble())
                values.put(IConnectContract.DealerVisitEntry.DEALER, item.toString())
                values.put(IConnectContract.DealerVisitEntry.DATE_TIME, time.toString())

                uploadDealerVisit((currentLatLong!!.latitude).toString(), (currentLatLong!!.longitude).toString(), item.toString(),time.toString() )
                // Insert a new row for Toto into the provider using the ContentResolver.
                // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
                // into the pets database table.
                // Receive the new content URI that will allow us to access Toto's data in the future.
                val uri = contentResolver.insert(IConnectContract.DealerVisitEntry.CONTENT_URI, values)
                Log.i("INSERT DEALER_VISIT", uri.toString())

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            insertDealerVisit()
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        // TODO Auto-generated method stub
    }
    private fun uploadDealerVisit(lat: String, long: String, dealer: String, dateTine: String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_DEALER_VISIT,
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
                params["lat"] = lat
                params["long"] = long
                params["dealer"] = dealer
                params["dateTine"] = dateTine
                return params
            }

            /*
             *pass files using below method
             * */
            override fun getByteData(): Map<String, DataPart>? {
                val params = HashMap<String, DataPart>()
                //params["emp_image"] = DataPart("employee_1.png", getFileDataFromDrawable(bitmap))
                return params
            }
        }


        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this@DealerVisit)
        rQueue!!.add(volleyMultipartRequest)
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
