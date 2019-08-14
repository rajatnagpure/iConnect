package com.qisystems.iconnect.todolist

import android.app.DatePickerDialog
import android.app.ListActivity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.qisystems.iconnect.*
import com.qisystems.iconnect.DataPart
import com.qisystems.iconnect.data.IConnectContract
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.HashMap

class Todolist : ListActivity() {

  //  private var imageView: ImageView? = null
    private var rQueue: RequestQueue? = null
    private var arraylist: ArrayList<HashMap<String, String>>? = null

    private var mDisplayDate: TextView? = null
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    internal var addtext: EditText?= null
    internal var listview: ListView? = null
    internal var addtext1: ListView? = null
    var selectedDate:String = "N/A"
    var todoListString:String = "Empty"

    internal var itemlist = ArrayList<String>()
    internal var adapter: ArrayAdapter<String>?=null
    internal var add: Button?=null
    internal var del: Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.qisystems.iconnect.R.layout.activity_todolist)
        intiViews()
        mDisplayDate = findViewById(com.qisystems.iconnect.R.id.tvdate) as TextView
        val sd = SimpleDateFormat("dd/MM/yyyy")
        val date = Date()
        mDisplayDate!!.text = sd.format(date)

        mDisplayDate!!.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(
                this@Todolist,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,
                month,
                day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        mDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            var month = month
            month = month + 1
            val date = "$dayOfMonth/$month/$year"
            mDisplayDate!!.text = date
            selectedDate = date.toString()
        }
    }

    private fun intiViews() {

        add = findViewById(com.qisystems.iconnect.R.id.btnAdd) as Button
        del = findViewById(com.qisystems.iconnect.R.id.btnDel) as Button
        addtext = findViewById(com.qisystems.iconnect.R.id.txtItem) as EditText

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice, itemlist
        )

        add!!.setOnClickListener {
            itemlist.add(addtext!!.text.toString())

            addtext!!.setText("")

            adapter!!.notifyDataSetChanged()
        }

        del!!.setOnClickListener {
            val res = listView
                .checkedItemPositions
            val itemCount = listView.count
            for (i in itemCount - 1 downTo 0) {
                if (res.get(i)) {
                    adapter!!.remove(itemlist[i])

                }
            }
            res.clear()
            adapter!!.notifyDataSetChanged()
        }

        listAdapter = adapter
    }

    fun subdetails(v: View) {
        Log.i("TODO STRING", "submit called")
        todoListString = ""
        var i=1
        for (item in itemlist) {
            todoListString += i.toString() + ". " +item + ". | "
            //Toast.makeText(this@Todolist, " " + a[i], Toast.LENGTH_LONG).show()
            //val db = DB_iconnect(this)
            //val isInserted = db.adddetail(itemlist[i])
            // Boolean a=db.login_emp(uname);
           // Toast.makeText(this@activity_todolist, "backend response $isInserted", Toast.LENGTH_LONG).show()
            i++
        }
        /**
         * Helper method to insert Demo Plot data into the database.
         */
        fun insertTodoList() {
            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            val values = ContentValues ()
            values.put(IConnectContract.TodoListEntry.DATE, selectedDate.toString())
            values.put(IConnectContract.TodoListEntry.TODO_LIST, todoListString.toString())

            uploadTodoList(todoListString, selectedDate.toString())

            // Insert a new row for Toto into the provider using the ContentResolver.
            // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
            // into the pets database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            val uri = contentResolver.insert(IConnectContract.TodoListEntry.CONTENT_URI, values)
            Log.i("INSERT TODO_LIST", uri.toString())
        }
        insertTodoList()
        Log.i("TODO STRING", todoListString)
        Log.i("TODO STRING", selectedDate)
        val iu = Intent(this@Todolist, MainActivity::class.java)
        startActivity(iu)
    }

    private fun uploadTodoList(todoList: String, dateTine: String) {

        val volleyMultipartRequest = object : VolleyMultipartRequest(
            Request.Method.POST, Constants.UPLOAD_TODO_LIST,
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
                params["todoList"] = todoList
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
        rQueue = Volley.newRequestQueue(this@Todolist)
        rQueue!!.add(volleyMultipartRequest)
    }

    companion object {
        private val TAG = "activity_todolist"
    }
}
