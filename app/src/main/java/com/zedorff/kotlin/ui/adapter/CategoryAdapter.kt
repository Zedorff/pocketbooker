package com.zedorff.kotlin.ui.adapter

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zedorff.kotlin.MainActivity
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.Category
import com.zedorff.kotlin.ui.fragment.CATEGORY_ID
import com.zedorff.kotlin.ui.fragment.TransactionFragment

class CategoryAdapter(private var mCategoryList: List<Category>): RecyclerView.Adapter<CategoryAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_category, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val category = mCategoryList[position]
        viewHolder.mCategoryName.text = category.name
    }

    override fun getItemCount(): Int = mCategoryList.size


    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var mCategoryName: TextView = itemView.findViewById(R.id.category_name) as TextView

        init {
            item.setOnClickListener {
                val fragment = TransactionFragment()
                val bundle = Bundle()
                bundle.putString(CATEGORY_ID, (mCategoryList[adapterPosition].id))
                fragment.arguments = bundle
                (item.context as MainActivity).replaceFragment(fragment)
            }
        }

    }
}