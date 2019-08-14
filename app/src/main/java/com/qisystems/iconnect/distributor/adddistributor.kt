package com.qisystems.iconnect.distributor


import android.content.ContentValues
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.DataPart
import com.qisystems.iconnect.data.IConnectContract
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.ArrayList
import java.util.HashMap

class adddistributor : AppCompatActivity() {

    //private var imageView: ImageView? = null
    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null

    //internal var mydb1!!: DB_iconnect?=null
   // internal var txtdname: EditText?=null
   // internal var txtcname: EditText?=null
   // internal var txtphone: EditText?=null
   // internal var txtaddress: EditText?=null
   // internal var txtcity: EditText?=null
   // internal var txtdistrict: EditText?=null
    //internal var txtstate: EditText?=null
   // internal var btn_submit: Button?=null

    private var sipnnerProduct = "N/A"
    private var editableNameField = "N/A"
    private var editableContact = "N/A"
    private var editableVillage = "N/A"
    private var editableDistrict = "N/A"
    private var editableState = "N/A"
    private var editableCrop = "N/A"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_adddistributor)
       // mydb1 = DB_iconnect(this)

         val txtdname = ((findViewById(com.qisystems.iconnect.R.id.txt_dname))as EditText)
         val txtcname = ((findViewById(com.qisystems.iconnect.R.id.txt_cname))as EditText)
         val txtphone = ((findViewById(com.qisystems.iconnect.R.id.txt_phone))as EditText)
        val txtaddress = ((findViewById(com.qisystems.iconnect.R.id.txt_address))as EditText)
        val txtcity = ((findViewById(com.qisystems.iconnect.R.id.txt_city))as EditText)
       val txtdistrict = ((findViewById(com.qisystems.iconnect.R.id.txt_district)) as EditText)
        val txtstate = ((findViewById(com.qisystems.iconnect.R.id.txt_state)) as EditText)
         val btn_submit = ((findViewById(com.qisystems.iconnect.R.id.button1)) as Button)


        val demoPlotTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                sipnnerProduct = txtdname.getText().toString()
                editableNameField = txtcname.getText().toString()
                editableContact = txtphone.getText().toString()
                editableVillage = txtaddress.getText().toString()
                editableDistrict = txtcity.getText().toString()
                editableState = txtdistrict.getText().toString()
                editableCrop = txtstate.getText().toString()

                //checkForEditables = (!editableNameField.isEmpty() && !editableContact.isEmpty() && !editableNameField.isEmpty() && !editableVillage.isEmpty() && !editableDistrict.isEmpty() && !editableState.isEmpty() && !editableDemoArea.isEmpty())
            }
            override fun afterTextChanged(s: Editable) {
            }
        }

        txtdname.addTextChangedListener(demoPlotTextWatcher)
        txtcname.addTextChangedListener(demoPlotTextWatcher)
        txtphone.addTextChangedListener(demoPlotTextWatcher)
        txtaddress.addTextChangedListener(demoPlotTextWatcher)
        txtcity.addTextChangedListener(demoPlotTextWatcher)
        txtdistrict.addTextChangedListener(demoPlotTextWatcher)
        txtstate.addTextChangedListener(demoPlotTextWatcher)


        btn_submit!!.setOnClickListener {

            Toast.makeText(applicationContext,"Distributor Submission SUCCESSFUL!!!", Toast.LENGTH_LONG).show()


            fun insertDistributor() {
                // Create a ContentValues object where column names are the keys,
                // and Toto's pet attributes are the values.
                val values = ContentValues ()
                values.put(IConnectContract.DistributorEntry.DISTRIBUTOR_NAME, sipnnerProduct.toString())
                values.put(IConnectContract.DistributorEntry.COMPANY_NAME, editableNameField.toString())
                values.put(IConnectContract.DistributorEntry.PHONE_NO, editableContact.toString())
                values.put(IConnectContract.DistributorEntry.ADDRESS, editableVillage.toString())
                values.put(IConnectContract.DistributorEntry.CITY, editableDistrict.toString())
                values.put(IConnectContract.DistributorEntry.DISTRICT, editableState.toString())
                values.put(IConnectContract.DistributorEntry.STATE, editableCrop.toString())

                uploadDistributor(sipnnerProduct,editableNameField,editableContact,editableVillage,editableDistrict, editableState,  editableCrop)
                // Insert a new row for Toto into the provider using the ContentResolver.
                // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
                // into the pets database table.
                // Receive the new content URI that will allow us to access Toto's data in the future.
                val uri = contentResolver.insert(IConnectContract.DistributorEntry.CONTENT_URI, values)
                Log.i("INSERT Distributor", uri.toString())
            }
            insertDistributor()
        }

    }
    private fun uploadDistributor(distributorName: String, companyName: String, phoneNo: String, address: String, city: String, district: String, state: String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_DISTRIBUTOR,
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
                params["distributorName"] = distributorName
                params["companyName"] = companyName
                params["phoneNo"] = phoneNo
                params["address"] = address
                params["city"] = city
                params["district"] = district
                params["state"] = state
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
        rQueue = Volley.newRequestQueue(this@adddistributor)
        rQueue!!.add(volleyMultipartRequest)
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}
