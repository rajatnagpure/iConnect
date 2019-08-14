package com.qisystems.iconnect.OrderBook

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.qisystems.iconnect.Constants
import com.qisystems.iconnect.R
import com.qisystems.iconnect.SharedPref
import com.qisystems.iconnect.demoplot.demoPlotAdapter
import com.qisystems.iconnect.demoplot.demoPlotItem
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

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class OrderListView : AppCompatActivity() {

    private var Recycleview: RecyclerView? = null
    private var oAdapter: OrderAdapter? = null
    private var oLayoutManager: RecyclerView.LayoutManager? = null
    internal var txt_dt: TextView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list_view)

        loadOrderBook()

        txt_dt = findViewById(R.id.txt_OrderDate)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = Date()
        txt_dt!!.text = dateFormat.format(date)

    }

    override fun onResume() {
        super.onResume()
        loadOrderBook()
    }
    private fun loadOrderBook() {

        class SendPostReqAsyncTask : AsyncTask<String, Void, String>() {

            private var loadingDialog: Dialog? = null

            override fun onPreExecute() {
                super.onPreExecute()
                loadingDialog = ProgressDialog.show(this@OrderListView, "Please wait", "Loading...")
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
                    val httpPost = HttpPost(Constants.DOWNLOAD_ORDERBOOK)
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
                Log.i("orderbooklist",result.toString())
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
                            Log.i("orderlist Activity", "Success List Array Made")
                            var orderList = ArrayList<OrderItem>()
                            orderList = extractFeatureFromJson(result)!!
                            Recycleview = findViewById(R.id.order_list) as RecyclerView
                            Recycleview!!.setHasFixedSize(true)
                            oLayoutManager = LinearLayoutManager(this@OrderListView)
                            oAdapter = OrderAdapter(orderList)

                            Recycleview!!.layoutManager = oLayoutManager
                            Recycleview!!.adapter = oAdapter


                            oAdapter!!.setOnItemClickListener(object : OrderAdapter.OnItemClickListener {
                                override fun OnItemClick(i: Int) {
                                    orderList[i]
                                    Toast.makeText(applicationContext, "Item Clicked !" + orderList[i], Toast.LENGTH_SHORT).show()
                                }

                                override fun OnDeleteClick(i: Int) {
                                    orderList.removeAt(i)
                                    oAdapter!!.notifyItemRemoved(i)
                                }

                                override fun OnEditClick(i: Int) {

                                    val myIntent = Intent(
                                        this@OrderListView,
                                        OrderBook::class.java
                                    )
                                    startActivity(myIntent)
                                    Log.i("Main Activity", "Starting new activity")
                                }
                            }
                            )
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
    private fun extractFeatureFromJson(stringjson:String):ArrayList<OrderItem>? {
        // If the JSON string is empty or null, then return early.
        Log.i("orderbooklist", stringjson+"b,sdcbjhsdabh")
        if (TextUtils.isEmpty(stringjson))
        {
            return null
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        val stringlist = ArrayList<OrderItem>()
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try
        {
            // Create a JSONObject from the JSON response string
            val baseJsonResponse = JSONObject(stringjson)
            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            val listArray = baseJsonResponse.getJSONArray("orderbooklist")
            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (i in 0 until listArray.length())
            {
                val currentItem = listArray.getJSONObject(i)
                val pname = currentItem.getString("pname")
                val qty = currentItem.getInt("qty")
                val rate = currentItem.getInt("rate")
                val subt = currentItem.getInt("subt")
                Log.i("orderlist", pname+qty+rate+subt)

                val tada = OrderItem(pname,qty,rate,subt, R.drawable.ic_edit_black_24dp, R.drawable.ic_delete_black_24dp)
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






