package com.qisystems.iconnect.Feedback

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.qisystems.iconnect.R
import com.sendbird.android.UserMessage

internal class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var messageText: TextView
    var timeText: TextView
    var nameText: TextView
    var profileImage: ImageView

    init {
        messageText = itemView.findViewById(R.id.text_message_body) as TextView
        timeText = itemView.findViewById(R.id.text_message_time) as TextView
        nameText = itemView.findViewById(R.id.text_message_name) as TextView
        profileImage = itemView.findViewById(R.id.image_message_profile) as ImageView
    }

    fun bind(message: UserMessage) {
        messageText.setText(message.getMessage())

        // Format the stored timestamp into a readable String using method.
        //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        nameText.setText(message.getSender().getNickname())

        // Insert the profile image from the URL into the ImageView.
        //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
    }
}
