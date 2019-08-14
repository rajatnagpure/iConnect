package com.qisystems.iconnect.OrderBook

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.qisystems.iconnect.R

import java.util.ArrayList

class OrderAdapter(private val mOrderList: ArrayList<OrderItem>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var mlistener: OnItemClickListener? = null

    interface OnItemClickListener {

        fun OnItemClick(i: Int)
        fun OnDeleteClick(i: Int)
        fun OnEditClick(i: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mlistener = listener


    }
    class OrderViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

        var p_name: TextView
        var q_ty: TextView
        var r_t: TextView
        var s_tot: TextView
        var img_edit: ImageView
        var img_del: ImageView


        init {

            p_name = itemView.findViewById(R.id.text_list_prdname)
            q_ty = itemView.findViewById(R.id.text_list_qty)
            r_t = itemView.findViewById(R.id.text_list_rate)
            s_tot = itemView.findViewById(R.id.text_list_subtot)
            img_edit = itemView.findViewById(R.id.img_edit)
            img_del = itemView.findViewById(R.id.img_delete)

            itemView.setOnClickListener {
                if (listener != null) {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(pos)
                    }

                }
            }

            img_del.setOnClickListener {
                if (listener != null) {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.OnDeleteClick(pos)
                    }

                }
            }



            img_edit.setOnClickListener {
                if (listener != null) {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.OnEditClick(pos)
                    }

                }
            }

        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): OrderViewHolder {

        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.order_item, viewGroup, false)
        return OrderViewHolder(v, mlistener)
    }

    override fun onBindViewHolder(orderViewHolder: OrderViewHolder, i: Int) {

        val currentItem = mOrderList[i]

        orderViewHolder.p_name.text = currentItem.pname
        orderViewHolder.q_ty.text = Integer.toString(currentItem.qty)
        orderViewHolder.r_t.text = Integer.toString(currentItem.rate)
        orderViewHolder.s_tot.text = Integer.toString(currentItem.subt)
        orderViewHolder.img_edit.setImageResource(currentItem.imgRes1)
        orderViewHolder.img_del.setImageResource(currentItem.imgRes2)
    }

    override fun getItemCount(): Int {
        return mOrderList.size
    }

}
