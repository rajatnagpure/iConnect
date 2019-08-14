package com.qisystems.iconnect.Authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.qisystems.iconnect.R

class Splash : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val myThread = object : Thread() {

            override fun run() {

                try {
                    Thread.sleep(2000)
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }


        }

        myThread.start()
    }
}
