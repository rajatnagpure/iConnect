package com.qisystems.iconnect.Feedback


class ChatAppMsgDTO(// Message type.
    var msgType: String?, // Message content.
    var msgContent: String?
) {
    companion object {

        val MSG_TYPE_SENT = "MSG_TYPE_SENT"

        val MSG_TYPE_RECEIVED = "MSG_TYPE_RECEIVED"
    }
}

