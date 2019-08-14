package com.qisystems.iconnect.Feedback

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.DataPart
import com.qisystems.iconnect.data.IConnectContract
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Feedback : AppCompatActivity() {

    //private var imageView: ImageView? = null
    private val GALLERY = 1
    private var rQueue: RequestQueue? = null
    private var arraylist: java.util.ArrayList<HashMap<String, String>>? = null

    private val SENDER_AUTHORITY = 1
    private val RECEIVER_AUTHORITY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_feedback)

        title = "dev2qa.com - Android Chat App Example"

        // Get RecyclerView object.
        val msgRecyclerView = findViewById(com.qisystems.iconnect.R.id.chat_recycler_view) as RecyclerView

        // Set RecyclerView layout manager.
        val linearLayoutManager = LinearLayoutManager(this)
        msgRecyclerView.layoutManager = linearLayoutManager
        // Create the initial data list.
        val msgDtoList = ArrayList<ChatAppMsgDTO>()

        if(!ApplicationUtils.getBooleanPreferenceValue(this,"isFirstTimeExecution")){
            Log.d("First mssg", "First time Execution")
            ApplicationUtils.setBooleanPreferenceValue(this,"isFirstTimeExecution",true)
            insertreceiverChat("hello")
            insertreceiverChat("How Ccan I Help You?")
        }
        val msgDto = ChatAppMsgDTO(
            ChatAppMsgDTO.MSG_TYPE_RECEIVED,"hello"
        )
        msgDtoList.add(msgDto)

        val msgR = ChatAppMsgDTO(
            ChatAppMsgDTO.MSG_TYPE_RECEIVED,"How Ccan I Help You?"
        )
        msgDtoList.add(msgR)
        // Create the data adapter with above data list.
        val chatAppMsgAdapter = MessageListAdapter(msgDtoList)

        // Set data adapter to RecyclerView.
        msgRecyclerView.adapter = chatAppMsgAdapter

        val msgInputText = findViewById(com.qisystems.iconnect.R.id.chat_input_msg) as EditText

        val msgSendButton = findViewById(com.qisystems.iconnect.R.id.chat_send_msg) as ImageView

        msgSendButton.setOnClickListener {
            val msgContent = msgInputText.text.toString()
            if (!TextUtils.isEmpty(msgContent)) {
                // Add a new sent message to the list.
                val msgDto = ChatAppMsgDTO(
                    ChatAppMsgDTO.MSG_TYPE_SENT,
                    msgContent
                )
                msgDtoList.add(msgDto)

                /**
                 * Helper method to insert senders chat data into the database.
                 */
                fun insertsendersChat() {
                    val values = ContentValues ()
                    values.put(IConnectContract.FeedbackEntry.MESSAGE, msgContent.toString())
                    values.put(IConnectContract.FeedbackEntry.AUTHORITY, SENDER_AUTHORITY)

                    val c = Calendar.getInstance()
                    System.out.println("Current time => " + c.time)
                    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val time = df.format(c.time)
                    uploadchat(msgContent.toString(), time)
                    val uri = contentResolver.insert(IConnectContract.FeedbackEntry.CONTENT_URI, values)
                    Log.i("INSERT MESSAGE", uri.toString())
                }
                insertsendersChat()

                val newMsgPosition = msgDtoList.size - 1

                // Notify recycler view insert one new data.
                chatAppMsgAdapter.notifyItemInserted(newMsgPosition)

                // Scroll RecyclerView to the last message.
                msgRecyclerView.scrollToPosition(newMsgPosition)

                // Empty the input edit text box.
                msgInputText.setText("")
            }
        }
    }
    /**
     * Helper method to insert senders chat data into the database.
     */
    fun insertreceiverChat(mssg: String) {
        val values = ContentValues ()
        values.put(IConnectContract.FeedbackEntry.MESSAGE, mssg.toString())
        values.put(IConnectContract.FeedbackEntry.AUTHORITY, RECEIVER_AUTHORITY)

        val uri = contentResolver.insert(IConnectContract.FeedbackEntry.CONTENT_URI, values)
        Log.i("INSERT MESSAGE", uri.toString())
    }
    private fun uploadchat(message: String, authority: String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_SENDERS_CHAT,
            Response.Listener { response ->
                Log.d("ressssssoo", String(response.data))
                rQueue!!.cache.clear()
                try {
                    val jsonObject = JSONObject(String(response.data))
                    Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    jsonObject.toString().replace("\\\\", "")

                    if (jsonObject.getString("status") == "true") {

                        arraylist = java.util.ArrayList()
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
                params["message"] = message
                params["authority"] = authority
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
        rQueue = Volley.newRequestQueue(this@Feedback)
        rQueue!!.add(volleyMultipartRequest)
    }
    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}

