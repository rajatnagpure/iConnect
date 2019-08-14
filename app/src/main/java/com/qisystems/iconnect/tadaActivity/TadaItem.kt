package com.qisystems.iconnect.tadaActivity


class TadaItem {

    val status:Int

    val fromPlace:String

    val toPlace:String

    val distance:String

    val mode:String

    val expenses:String

    val otherExpenses:String

    val date:String


    constructor(fromPlace: String, toPlcae:  String, distance: String, mode: String, expenses: String, otherExpenses: String, date:   String,status:Int ) {
        this.fromPlace = fromPlace
        this.toPlace = toPlcae
        this.distance = distance
        this.mode = mode
        this.expenses = expenses
        this.otherExpenses = otherExpenses
        this.date = date
        this.status = status
    }
}