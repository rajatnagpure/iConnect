package com.qisystems.iconnect.scheme

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.qisystems.iconnect.R

/**
 * Created by Belal on 7/15/2017.
 */

class SchemeAdapter(private val schemeList: List<Scheme_model>, private val context: Context) :
    RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchemeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.scheme_list_view, parent, false)
        return SchemeViewHolder(v)
    }

    override fun onBindViewHolder(holder: SchemeViewHolder, position: Int) {
        val schemeModel = schemeList[position]
        holder.textViewName.text = schemeModel.name
        holder.textViewRealName.setText(schemeModel.realName)
        holder.textViewTeam.setText(schemeModel.team)
        holder.textViewCreatedBy.setText(schemeModel.createdBy)
        Glide.with(context).load(schemeModel.imageUrl)
        holder.linearLayout.visibility = View.GONE

        //if the position is equals to the item position which is to be expanded
        if (currentPosition == position) {
            //creating an animation
            val slideDown = AnimationUtils.loadAnimation(context, R.anim.scheme_anim)

            //toggling visibility
            holder.linearLayout.visibility = View.VISIBLE

            //adding sliding effect
            holder.linearLayout.startAnimation(slideDown)
        }

        holder.textViewName.setOnClickListener {
            //getting the position of the item to expand it
            currentPosition = position

            //reloding the list
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return schemeList.size
    }

    inner class SchemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView
        var textViewRealName: TextView
        var textViewTeam: TextView
        var textViewCreatedBy: TextView
        var imageView: ImageView
        var linearLayout: LinearLayout

        init {

            textViewName = itemView.findViewById(R.id.textViewName) as TextView
            textViewRealName = itemView.findViewById(R.id.textViewRealName) as TextView
            textViewTeam = itemView.findViewById(R.id.textViewTeam) as TextView
            textViewCreatedBy = itemView.findViewById(R.id.textViewCreatedBy) as TextView
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            linearLayout = itemView.findViewById(R.id.linearLayout) as LinearLayout
        }
    }

    companion object {

        private var currentPosition = 0
    }
}