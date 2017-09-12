package com.zedorff.kotlin.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zedorff.kotlin.R
import com.zedorff.kotlin.daoWrappers.CategoryDao
import com.zedorff.kotlin.ui.adapter.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_category_list.view.*

const val CATEGORY_ID = "category_id"

class CategoryFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private var mCategoryDao = CategoryDao()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.category_list_recycler
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mCategoryDao.getAll()
                .subscribe({ categories ->
                    mRecyclerView.adapter = CategoryAdapter(categories)
                })
    }

}