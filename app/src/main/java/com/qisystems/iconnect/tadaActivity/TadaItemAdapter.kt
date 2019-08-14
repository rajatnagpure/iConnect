package com.qisystems.iconnect.tadaActivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.qisystems.iconnect.R

import java.util.ArrayList

class TadaItemAdapter(context: Context, TadaItems: ArrayList<TadaItem>) : ArrayAdapter<TadaItem>(context, 0, TadaItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        // Get the {@link TadaItem} object located at this position in the list
        val currentItem = getItem(position)

        val textFromTo = listItemView!!.findViewById(R.id.text_from_to) as TextView
        textFromTo.text = """${currentItem.fromPlace} to ${currentItem.toPlace}"""

        val textExpenses = listItemView.findViewById(R.id.text_expenses) as TextView
        textExpenses.setText(currentItem.expenses)

        val textDistance = listItemView.findViewById(R.id.text_distance) as TextView
        textDistance.setText(currentItem.distance)

        val textOtherExpenses = listItemView.findViewById(R.id.text_other_expenses) as TextView
        textOtherExpenses.setText(currentItem.otherExpenses)

        val textMode = listItemView.findViewById(R.id.text_mode) as TextView
        textMode.text = currentItem.mode

        val textDate = listItemView.findViewById(R.id.text_date) as TextView
        textDate.text = currentItem.date

        val tadaStatus = listItemView.findViewById(R.id.tada_status) as ImageView
        if(currentItem.status == 0)
        {
            tadaStatus.setImageResource(R.drawable.ic_access_time_black_24dp)
        }
        else if(currentItem.status == 1)
        {
            tadaStatus.setImageResource(R.drawable.ic_check_black_24dp)
        }
        else
        {
            tadaStatus.setImageResource(R.drawable.ic_close_black_24dp)
        }

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView
    }
}