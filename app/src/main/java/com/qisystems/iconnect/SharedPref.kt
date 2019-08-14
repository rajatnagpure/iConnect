package com.qisystems.iconnect

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    private var mSharedPref: SharedPreferences? = null
    const val NAME = "NAME"
    const val MOBILE = "MOBILE"
    const val ID = "ID"
    const val USERNAME = "USERNAME"
    const val PASSWORD = "PASSWORD"
    const val EMAIL = "EMAIL"
    const val DESIGNATION = "DESIGNATION"
    const val IMAGE_NAME = "IMAGE_NAME"
    const val PRODUCT_SET = "PRODUCT_SET"
    const val DEALER_SET = "DEALER_SET"
    const val AGENCY_SET = "AGENCY_SET"

    //INITIALISE shared preference
    fun init(context: Context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    //READ WRITE STRING
    fun read(key: String, defValue: String): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String, value: String) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    //READ WRITE BOOLEAN
    fun read(key: String, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    //READ WRITE INTEGER
    fun read(key: String, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun write(key: String, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!).apply()
    }

    //READ WRITE STRING SET
    fun read(key: String, defValue: Set<String>): Set<String>? {
        return mSharedPref!!.getStringSet(key, defValue)
    }

    fun write(key: String, value: Set<String>?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putStringSet(key, value!!).apply()
    }

}