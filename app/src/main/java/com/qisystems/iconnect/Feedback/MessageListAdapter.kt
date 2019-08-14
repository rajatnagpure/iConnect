package com.qisystems.iconnect.Feedback

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.qisystems.iconnect.R

import kotlin.collections.ArrayList

/**
 * Created by Jerry on 12/19/2017.
 */

class MessageListAdapter(msgDtoList: List<ChatAppMsgDTO>) : RecyclerView.Adapter<ChatAppMsgViewHolder>() {

    private var msgDtoList: List<ChatAppMsgDTO>? = null

    init {
        this.msgDtoList = msgDtoList
    }

    override fun onBindViewHolder(holder: ChatAppMsgViewHolder, position: Int) {
        val msgDto = this.msgDtoList!!.get(position)
        // If the message is a received message.


        if (ChatAppMsgDTO.MSG_TYPE_RECEIVED == msgDto.msgType) {
            // Show received message in left linearlayout.
            holder.leftMsgLayout!!.visibility = LinearLayout.VISIBLE
            holder.leftMsgTextView!!.text = msgDto.msgContent
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgLayout!!.visibility = LinearLayout.GONE
        } else if (ChatAppMsgDTO.MSG_TYPE_SENT == msgDto.msgType) {
            // Show sent message in right linearlayout.
            holder.rightMsgLayout!!.visibility = LinearLayout.VISIBLE
            holder.rightMsgTextView!!.text = msgDto.msgContent
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgLayout!!.visibility = LinearLayout.GONE
        }// If the message is a sent message.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAppMsgViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.activity_chat_app_item, parent, false)
        return ChatAppMsgViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (msgDtoList == null) {

            msgDtoList = ArrayList<ChatAppMsgDTO>()
        }
        return msgDtoList!!.size
    }
}
