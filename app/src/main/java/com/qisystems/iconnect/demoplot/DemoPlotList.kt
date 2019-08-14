package com.qisystems.iconnect.demoplot

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import com.qisystems.iconnect.Constants
import com.qisystems.iconnect.SharedPref
import kotlinx.android.synthetic.main.activity_demo_plot_list.*
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class DemoPlotList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_demo_plot_list)

        loadDemoPlots()


        fabbutton.setOnClickListener { view ->
            val intent = Intent(this, DemoPlot::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        loadDemoPlots()
    }
    private fun loadDemoPlots() {

        class SendPostReqAsyncTask : AsyncTask<String, Void, String>() {

            var answer: EditText? = null
            private var loadingDialog: Dialog? = null

            override fun onPreExecute() {
                super.onPreExecute()
                loadingDialog = ProgressDialog.show(this@DemoPlotList, "Please wait", "Loading...")
            }

            override fun doInBackground(vararg params: String): String? {

                // String ans = e1.getText().toString();
                val nameValuePairs = java.util.ArrayList<NameValuePair>()
                nameValuePairs.add(BasicNameValuePair("id", SharedPref.read(SharedPref.ID, 0).toString()))

                var inputStream: InputStream? = null
                var result: String? = null
                try {
                    val httpParameters = BasicHttpParams()
                    val timeoutConnection = 3000
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection)
                    val timeoutSocket = 5000
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket)

                    val httpClient = DefaultHttpClient(httpParameters)
                    val httpPost = HttpPost(Constants.DOWNLOAD_DEMO_PLOTS)
                    httpPost.entity = UrlEncodedFormEntity(nameValuePairs)
                    val response = httpClient.execute(httpPost)
                    val entity = response.entity
                    inputStream = entity.content
                    // json is UTF-8 by default
                    val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8") as Reader?, 8)
                    val sb = StringBuilder()

                    var line = reader.readLine()
                    while (line  != null) {
                        sb.append(line)
                        line = reader.readLine()
                    }
                    result = sb.toString()

                } catch (e: ClientProtocolException) {
                    // Toast.makeText(getActivity(), "exception"+e, Toast.LENGTH_LONG).show();

                } catch (e: IOException) {
                    //Toast.makeText(getActivity(), "exception"+e, Toast.LENGTH_LONG).show();
                }

                return result
            }

            override fun onPostExecute(result: String) {
                loadingDialog!!.dismiss()
                // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                var error = true
                try
                {
                    val json = JSONObject(result)
                    error = json.get("error") as Boolean
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
                loadingDialog!!.dismiss()
                if (error) {
                } else {
                    try {
                        runOnUiThread {
                            Log.i("DemoPlot Activity", "Success List Array Made")
                            val adapter = demoPlotAdapter (this@DemoPlotList, extractFeatureFromJson(result)!!)
                            val listView = findViewById<ListView>(com.qisystems.iconnect.R.id.demo_plot_list_view) as ListView
                            listView.adapter = adapter
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val sendPostReqAsyncTask = SendPostReqAsyncTask()
        sendPostReqAsyncTask.execute()
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
