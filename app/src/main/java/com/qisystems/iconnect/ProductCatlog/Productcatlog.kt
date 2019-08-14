package com.qisystems.iconnect.ProductCatlog


import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.github.barteksc.pdfviewer.PDFView
import com.qisystems.iconnect.R

class Productcatlog : AppCompatActivity() {
    internal var book1: PDFView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productcatlog)

        book1 = findViewById(R.id.pdfbook1) as PDFView

        book1!!.fromAsset("catlog.pdf").load()
    }
}
