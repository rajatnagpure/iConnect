package com.qisystems.iconnect.distributor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.qisystems.iconnect.R
import java.util.ArrayList

class DistributorAdapter (context: Context, demoPlotItems: ArrayList<DistributorItem>) : ArrayAdapter<DistributorItem>(context, 0, demoPlotItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.distributor_item, parent, false)
        }

        // Get the {@link TadaItem} object located at this position in the list
        val currentItem = getItem(position)

        val company_name = listItemView!!.findViewById(R.id.company_name) as TextView
        company_name.text = currentItem!!.cname

        val dist_name = listItemView.findViewById(R.id.dist_name) as TextView
        dist_name.setText(currentItem.dname)

        val dist_mob = listItemView.findViewById(R.id.dist_mob) as TextView
        dist_mob.setText(currentItem.dmob.toString())

        val dist_email = listItemView.findViewById(R.id.dist_email) as TextView
        dist_email.setText(currentItem.demail)

        val dist_address = listItemView.findViewById(R.id.dist_address) as TextView
        dist_address.text = currentItem.dadd
        val next_demo_text = listItemView.findViewById(R.id.next_demo_text) as TextView
        next_demo_text.text = currentItem.cname.get(0).toString()
        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView
    }
}
