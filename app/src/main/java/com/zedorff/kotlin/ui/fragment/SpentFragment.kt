package com.zedorff.kotlin.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zedorff.kotlin.MainActivity
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.CategoryDao
import com.zedorff.kotlin.model.SpentItemDao
import com.zedorff.kotlin.ui.adapter.SpentAdapter
import com.zedorff.kotlin.ui.recycler.VerticalSpacingDecorator
import kotlinx.android.synthetic.main.spent_list_fragment.view.*


class SpentFragment : Fragment() {

    lateinit var mRecyclerView: RecyclerView
    var categoryId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getLong(CATEGORY_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.spent_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.spent_list_recycler
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = SpentAdapter(CategoryDao.loadCategory(categoryId).getSpentItems(), View.OnClickListener {
//            (activity as MainActivity).replaceFragment()
        })
    }

}