package com.zedorff.kotlin.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.Category

class CategoryAdapter(private var mCategoryList: List<Category>, private var mOnClickListener: View.OnClickListener): RecyclerView.Adapter<CategoryAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.category_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val category = mCategoryList[position]
        viewHolder.mCategoryName.text = category.name
        with (viewHolder.mRoot) {
            tag = category
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mCategoryList.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mRoot: ViewGroup = itemView.findViewById(R.id.category_item_root) as ViewGroup
        var mCategoryName: TextView = itemView.findViewById(R.id.category_name) as TextView
    }
}