package com.qisystems.iconnect.demoplot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.qisystems.iconnect.R

import java.util.ArrayList

class demoPlotAdapter(context: Context, demoPlotItems: ArrayList<demoPlotItem>) : ArrayAdapter<demoPlotItem>(context, 0, demoPlotItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_demo_plot, parent, false)
        }

        // Get the {@link TadaItem} object located at this position in the list
        val currentItem = getItem(position)

        val farmerName = listItemView!!.findViewById(R.id.farmer_name) as TextView
        farmerName.text = currentItem!!.farmerNmae

        val contactText = listItemView.findViewById(R.id.farmer_contact) as TextView
        contactText.setText(currentItem.contact)

        val dateDemo = listItemView.findViewById(R.id.text_date) as TextView
        dateDemo.setText(currentItem.dateDemo)

        val dateNextDemo = listItemView.findViewById(R.id.next_demo_text) as TextView
        dateNextDemo.setText(currentItem.dateNextDemo)

        val crop = listItemView.findViewById(R.id.text_crop) as TextView
        crop.text = currentItem.crop

        val product = listItemView.findViewById(R.id.text_enter_product) as TextView
        product.text = currentItem.crop

        val demoArea = listItemView.findViewById(R.id.text_demo_area) as TextView
        demoArea.text = currentItem.demoArea

        val address = listItemView.findViewById(R.id.farmer_address) as TextView
        address.text = currentItem.village +", "+currentItem.district +", "+currentItem.state +"."

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView
    }
}