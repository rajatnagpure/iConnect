package com.qisystems.iconnect

internal class DataPart {
    var fileName: String ?=null
    var content: ByteArray ? = null
    val type: String? = null

    constructor() {}

    constructor(name: String, data: ByteArray) {
        fileName = name
        content = data
    }

}