package com.qisystems.iconnect.Authentication

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.qisystems.iconnect.Constants.Companion.SIGNUP_POST_URL
import com.qisystems.iconnect.InternetConnection
import com.qisystems.iconnect.R
import org.apache.http.HttpEntity
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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList


class SignUp : Activity() {

    //internal var mydb: DB_iconnect
    internal var txtname: EditText ?= null
    internal var txtuname: EditText ?= null
    internal var txtemail: EditText ?= null
    internal var txtphone: EditText ?= null
    internal var txtpass: EditText ?= null
    internal var txtcfpass: EditText ?= null
    internal var txtdesg: EditText ?= null
    internal var btn_reg: Button ?= null

    var name: String ?= null
    var mobile: String ?= null
    var password: String ?= null
    var email: String ?= null
    var designation: String ?= null
    var cp: String ?= null
    var username:String ?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        //mydb = DB_iconnect(this)

        txtname = findViewById(R.id.txt_fname)
        txtuname = findViewById(R.id.txt_uname)
        txtemail = findViewById(R.id.txt_email)
        txtphone = findViewById(R.id.txt_phone)
        txtdesg = findViewById(R.id.txt_desg)
        txtpass = findViewById(R.id.txt_pass)
        txtcfpass = findViewById(R.id.txt_cfpass)

        btn_reg = findViewById(R.id.button1) as Button





        btn_reg!!.setOnClickListener {
            //var isInserted = mydb.insertData(
            name = txtname!!.text.toString()
            username = txtuname!!.text.toString()
            email = txtemail!!.text.toString()
            mobile = txtphone!!.text.toString()
            designation = txtdesg!!.text.toString()
            password = txtpass!!.text.toString()
            cp = txtcfpass!!.text.toString()
            if (name == "") {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("")
                builder.setMessage("Please Enter FullName")
                builder.setPositiveButton("OKAY"){dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            } else if (password == "") {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("")
                builder.setMessage("Please Enter Password")
                builder.setPositiveButton("OKAY"){dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else if (mobile == "") {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("")
                builder.setMessage("Please Enter Mobile Number")
                builder.setPositiveButton("OKAY") { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
                else if (email.equals(""))
                {
                    val builder = AlertDialog.Builder(this@SignUp)
                    builder.setTitle("oops!")
                    builder.setMessage("Please Enter Email")
                    builder.setPositiveButton("OKAY") { dialog, which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("oops!")
                builder.setMessage("Please Enter Correct Email")
                builder.setPositiveButton("OKAY") { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                }
            else if (password != cp) {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("")
                builder.setMessage("Password And Conform Password Not Match")
                builder.setPositiveButton("OKAY") { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            } else if (mobile!!.length < 10 && mobile!!.length >= 11) {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("")
                builder.setMessage("Please Enter Valid Mobile Number")
                builder.setPositiveButton("OKAY") { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            } else if (!InternetConnection.checkConnection(this)) {
                val builder = AlertDialog.Builder(this@SignUp)
                builder.setTitle("")
                builder.setMessage("Please Check Internet Connection")
                builder.setPositiveButton("OKAY") { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else {
                serverAuthentication()
            }

        }

    }
    private fun serverAuthentication() {

        class SendPostReqAsyncTask : AsyncTask<String, Void, String>() {

            var answer: EditText? = null
            private var loadingDialog: Dialog? = null

            override fun onPreExecute() {
                super.onPreExecute()
                loadingDialog = ProgressDialog.show(this@SignUp, "Please wait", "Loading...")
            }

            override fun doInBackground(vararg params: String): String? {

                // String ans = e1.getText().toString();
                val nameValuePairs = ArrayList<NameValuePair>()
                nameValuePairs.add(BasicNameValuePair("name", name))
                nameValuePairs.add(BasicNameValuePair("username", username))
                nameValuePairs.add(BasicNameValuePair("mobile", mobile))
                nameValuePairs.add(BasicNameValuePair("password", password))
                nameValuePairs.add(BasicNameValuePair("email", email))
                nameValuePairs.add(BasicNameValuePair("designation", designation))

                var inputStream: InputStream? = null
                var result: String? = null
                try {
                    val httpParameters = BasicHttpParams()
                    val timeoutConnection = 3000
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection)
                    val timeoutSocket = 5000
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket)

                    val httpClient = DefaultHttpClient(httpParameters)
                    val httpPost = HttpPost(SIGNUP_POST_URL)
                    httpPost.entity = UrlEncodedFormEntity(nameValuePairs) as HttpEntity?
                    val response = httpClient.execute(httpPost)
                    val entity = response.entity
                    inputStream = entity.content
                    // json is UTF-8 by default
                    val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"), 8)
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
                var s = false
                try
                {
                    val json = JSONObject(result)
                    s = json.get("error") as Boolean
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
                loadingDialog!!.dismiss()
                if (s) {
                    Toast.makeText(applicationContext, "Oops Something went wrong!!!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Successfully Registered", Toast.LENGTH_LONG).show()
                    val myIntent = Intent(
                        this@SignUp,
                        Login::class.java
                    )
                    startActivity(myIntent)
                }


            }
        }

        val sendPostReqAsyncTask = SendPostReqAsyncTask()
        sendPostReqAsyncTask.execute()
    }

}



