package com.qisystems.iconnect.scheme

class Scheme_model{
    val name: String
    val realName: String
    val team: String
    val createdBy: String
    val imageUrl: String

    constructor( name: String, realName: String, team: String, createdBy: String, imageUrl: String){
        this.name = name
        this.realName=realName
        this.team=team
        this.createdBy=createdBy
        this.imageUrl=imageUrl
    }
}