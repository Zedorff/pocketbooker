package com.zedorff.kotlin.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.SpentItem
import kotlinx.android.synthetic.main.spent_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class SpentAdapter(private var mSpentList: List<SpentItem>, private var mOnClickListener: View.OnClickListener) : RecyclerView.Adapter<SpentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.spent_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val spentItem = mSpentList[position]
        viewHolder.mSpentName.text = spentItem.name
        viewHolder.mSpentCategory.text = spentItem.category!!.name
        viewHolder.mSpentCost.text = "-${spentItem.cost.toString()} UAH"
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        simpleDateFormat.timeZone = TimeZone.getDefault()
        viewHolder.mSpentDate.text = simpleDateFormat.format(spentItem.date).toString()
        with(viewHolder.mSpentRoot) {
            tag = spentItem
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mSpentList.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mSpentRoot = itemView.spent_item_root
        var mSpentName = itemView.spent_name
        var mSpentCategory = itemView.spent_category
        var mSpentCost = itemView.spent_cost
        var mSpentDate = itemView.spent_date
    }
}