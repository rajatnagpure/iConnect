package com.qisystems.iconnect.Authentication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.qisystems.iconnect.Constants.Companion.APP_PATH_SD_CARD
import com.qisystems.iconnect.Constants.Companion.APP_THUMBNAIL_PATH_SD_CARD
import com.qisystems.iconnect.Constants.Companion.LOGIN_POST_URL
import com.qisystems.iconnect.R
import com.qisystems.iconnect.SharedPref
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
import java.util.ArrayList


class Login : Activity() {

    internal var un: EditText ?= null
    internal var ps: EditText ?= null
    internal var btnlogin: Button ?= null
    internal var employeeImg: ImageView?= null

    var username: String  = "N/A"
    var password: String = "N/A"

    //internal var mydb = DB_iconnect(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_login)
        //mydb = DB_iconnect(this)
        isStoragePermissionGranted()

        un = findViewById(com.qisystems.iconnect.R.id.un_login)
        ps = findViewById(com.qisystems.iconnect.R.id.ps_login)
        btnlogin = findViewById(com.qisystems.iconnect.R.id.btnlogin_1)
        employeeImg = findViewById(com.qisystems.iconnect.R.id.employee_img) as ImageView
        //var thubnail:Bitmap?=null
        /*thubnail =getThumbnail("employee_"+ SharedPref.read(SharedPref.IMAGE_NAME,"0").toString()+".png")
        if(thubnail!=null){
            employeeImg!!.setImageBitmap(thubnail)}*/


        btnlogin!!.setOnClickListener {
            username = un!!.text.toString()
            password = ps!!.text.toString()

            if (username == "") {
                val builder = AlertDialog.Builder(this@Login)
                builder.setTitle("")
                builder.setMessage("Please Enter UserName")
                builder.setPositiveButton("OKAY"){dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            } else if (password == "") {
                val builder = AlertDialog.Builder(this@Login)
                builder.setTitle("")
                builder.setMessage("Please Enter Password")
                builder.setPositiveButton("OKAY"){dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else if(username == SharedPref.read(SharedPref.USERNAME, "") && password ==  SharedPref.read(SharedPref.PASSWORD,"")){
                    val myIntent = Intent(
                        this@Login,
                        IdCard::class.java
                    )
                    startActivity(myIntent)
            } else{
                serverAuthentication()
                /*val myIntent = Intent(
                    this@Login,
                    IdCardemp::class.java
                )
                startActivity(myIntent)*/
            }
        }
    }
    private fun serverAuthentication() {

        class SendPostReqAsyncTask : AsyncTask<String, Void, String>() {

            var answer: EditText? = null
            private var loadingDialog: Dialog? = null

            override fun onPreExecute() {
                super.onPreExecute()
                loadingDialog = ProgressDialog.show(this@Login, "Please wait", "Loading...")
            }

            override fun doInBackground(vararg params: String): String? {

                // String ans = e1.getText().toString();
                val nameValuePairs = ArrayList<NameValuePair>()
                nameValuePairs.add(BasicNameValuePair("username", username))
                nameValuePairs.add(BasicNameValuePair("password", password))

                var inputStream: InputStream? = null
                var result: String? = null
                try {
                    val httpParameters = BasicHttpParams()
                    val timeoutConnection = 3000
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection)
                    val timeoutSocket = 5000
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket)

                    val httpClient = DefaultHttpClient(httpParameters)
                    val httpPost = HttpPost(LOGIN_POST_URL)
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
                var mobile:String = "N/A"
                var id:Int = -1
                var username: String = "N/A"
                var name:String = "N/A"
                var email:String = "N/A"
                var designation:String = "N/A"
                try
                {
                    val json = JSONObject(result)
                    error = json.get("error") as Boolean
                    mobile = json.get("mobile") as String
                    id = json.get("id") as Int
                    username = json.get("username") as String
                    name = json.get("name") as String
                    email = json.get("email") as String
                    designation = json.get("designation") as String
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
                loadingDialog!!.dismiss()
                if (error) {
                    Toast.makeText(applicationContext, "Oops Wrong Username or Password!!!", Toast.LENGTH_LONG).show()
                } else {
                    Log.i("Login",  username + name + email+ designation + mobile.toString())
                    //putting in shared preference
                    SharedPref.write(SharedPref.MOBILE,mobile)
                    SharedPref.write(SharedPref.ID,id)
                    SharedPref.write(SharedPref.USERNAME,username)
                    SharedPref.write(SharedPref.NAME,name)
                    SharedPref.write(SharedPref.EMAIL,email)
                    SharedPref.write(SharedPref.DESIGNATION,designation)
                    SharedPref.write(SharedPref.PASSWORD, password!!)


                    Toast.makeText(applicationContext, "Successfully Loged In", Toast.LENGTH_LONG).show()
                    val myIntent = Intent(
                        this@Login,
                        IdCard::class.java
                    )
                    startActivity(myIntent)
                }


            }
        }

        val sendPostReqAsyncTask = SendPostReqAsyncTask()
        sendPostReqAsyncTask.execute()
    }
    fun getThumbnail(filename:String): Bitmap? {
        val fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD
        var thumbnail: Bitmap?= null
        // Look for the file on the external storage
        try
        {
            if (isSdReadable())
            {
                thumbnail = BitmapFactory.decodeFile("$fullPath/$filename")
            }
        }
        catch (e:Exception) {
            Log.e("getThumbnail()", e.message)
            return null
        }
        // If no file on external storage, look in internal storage
        if (thumbnail == null)
        {
            try
            {
                val filePath = this.getFileStreamPath(filename)
                val fi = FileInputStream(filePath)
                thumbnail = BitmapFactory.decodeStream(fi)
            }
            catch (ex:Exception) {
                Log.e("getThumbnail()", ex.message)
                return null
            }
        }
        return thumbnail!!
    }
    fun isSdReadable():Boolean {
        var mExternalStorageAvailable = false
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // We can read and write the media
            mExternalStorageAvailable = true
            Log.i("isSdReadable", "External storage card is readable.")
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            // We can only read the media
            Log.i("isSdReadable", "External storage card is readable.")
            mExternalStorageAvailable = true
        }
        else
        {
            // Something else is wrong. It may be one of many other
            // states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false
        }
        return mExternalStorageAvailable
    }
    fun isStoragePermissionGranted():Boolean
    { val TAG = "Storage Permission"
        if (Build.VERSION.SDK_INT >= 23)
        {
            if ((this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED))
            {
                Log.v(TAG, "Permission is granted")
                return true
            }
            else
            {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        }
        else
        { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            return true
        } }
    fun signup(view: View) {


        val myIntent = Intent(
            this@Login,
            SignUp::class.java
        )
        startActivity(myIntent)
        Log.i("login Activity", "Starting new activity")
    }
}

