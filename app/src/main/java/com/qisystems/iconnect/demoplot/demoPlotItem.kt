package com.qisystems.iconnect.demoplot

class demoPlotItem {

    val farmerNmae:String

    val contact:String

    val dateDemo:String

    val dateNextDemo:String

    val crop:String

    val demoArea:String

    val product:String

    val village:String

    val district:String

    val state:String


    constructor(farmerNmae: String, contact:  String, dateDemo: String, dateNextDemo: String, crop: String, demoArea: String, product:   String, state: String, district: String, village:   String ) {
        this.farmerNmae = farmerNmae
        this.contact = contact
        this.dateDemo = dateDemo
        this.dateNextDemo = dateNextDemo
        this.crop = crop
        this.demoArea = demoArea
        this.product = product
        this.village = village
        this.district = district
        this.state = state
    }
}