package com.qisystems.iconnect.Feedback

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.qisystems.iconnect.R

/**
 * Created by Jerry on 12/20/2017.
 */

class ChatAppMsgViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    internal var leftMsgLayout: LinearLayout ?= null

    internal var rightMsgLayout: LinearLayout ?= null

    internal var leftMsgTextView: TextView ?= null

    internal var rightMsgTextView: TextView ?= null

    init {

        if (itemView != null) {
            leftMsgLayout = itemView.findViewById(R.id.chat_left_msg_layout) as LinearLayout
            rightMsgLayout = itemView.findViewById(R.id.chat_right_msg_layout) as LinearLayout
            leftMsgTextView = itemView.findViewById(R.id.chat_left_msg_text_view) as TextView
            rightMsgTextView = itemView.findViewById(R.id.chat_right_msg_text_view) as TextView
        }
    }
}

