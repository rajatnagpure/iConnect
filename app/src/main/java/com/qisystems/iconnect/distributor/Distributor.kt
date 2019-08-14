package com.qisystems.iconnect.distributor


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ListView
import com.qisystems.iconnect.R
import com.qisystems.iconnect.demoplot.DemoPlot
import com.qisystems.iconnect.demoplot.demoPlotItem
import kotlinx.android.synthetic.main.activity_distributor_list.*
import org.json.JSONException
import org.json.JSONObject

class Distributor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distributor_list)

        val listString = ArrayList<DistributorItem>()

            listString!!.add(DistributorItem("Company Name", "sdfsdf", 41947814, "sjhdf","jhsdfh"))

        val adapter = DistributorAdapter (this@Distributor, listString!!)
        val listView = findViewById<ListView>(com.qisystems.iconnect.R.id.distributor_list_view) as ListView
        listView.adapter = adapter

        fabbuttondistributor.setOnClickListener { view ->
            val intent = Intent(this, adddistributor::class.java)
            startActivity(intent)
        }

    }
    private fun extractFeatureFromJson(stringjson:String):ArrayList<demoPlotItem>? {
        // If the JSON string is empty or null, then return early.
        Log.i("tadalist", stringjson)
        if (TextUtils.isEmpty(stringjson))
        {
            return null
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        val stringlist = ArrayList<demoPlotItem>()
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try
        {
            // Create a JSONObject from the JSON response string
            val baseJsonResponse = JSONObject(stringjson)
            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            val listArray = baseJsonResponse.getJSONArray("demoplotlist")
            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (i in 0 until listArray.length())
            {
                val currentItem = listArray.getJSONObject(i)
                val farmerNmae = currentItem.getString("farmerNmae")
                val contact = currentItem.getString("contact")
                val dateDemo = currentItem.getString("dateDemo")
                val dateNextDemo = currentItem.getString("dateNextDemo")
                val crop = currentItem.getString("crop")
                val demoArea = currentItem.getString("demoArea")
                val product = currentItem.getString("product")
                val village = currentItem.getString("village")
                val district = currentItem.getString("district")
                val state = currentItem.getString("state")

                val tada = demoPlotItem(farmerNmae,contact,dateDemo,dateNextDemo,crop,demoArea,product,state,district,village)
                // Add the new {@link Earthquake} to the list of earthquakes.
                stringlist.add(tada)
            }
        }
        catch (e: JSONException) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e)
        }
        // Return the list of earthquakes
        return stringlist
    }
}
