package com.qisystems.iconnect.scheme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.Constants.Companion.DOWNLOAD_SCHEME
import com.qisystems.iconnect.R
import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

class Scheme : AppCompatActivity() {

    internal var recyclerView: RecyclerView?=null
    internal var adapter: SchemeAdapter?=null
    internal var schemeList: MutableList<Scheme_model>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheme)



        recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        schemeList = ArrayList<Scheme_model>()

        loadHeroes()
    }

    private fun loadHeroes() {
        val stringRequest = StringRequest(Request.Method.GET, DOWNLOAD_SCHEME,
            object : Response.Listener<String> {
                override fun onResponse(response: String) {
                    try {
                        val jsonArray = JSONArray(response)

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)

                            val hero = Scheme_model(
                                obj.getString("name"),
                                obj.getString("realname"),
                                obj.getString("team"),
                                obj.getString("createdby"),
                                obj.getString("imageurl")
                            )

                            schemeList?.add(hero)
                        }

                        adapter = schemeList?.let { SchemeAdapter(it, applicationContext) }
                        recyclerView!!.adapter = adapter

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            },


            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {

                }
            })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
    companion object{

    }
}