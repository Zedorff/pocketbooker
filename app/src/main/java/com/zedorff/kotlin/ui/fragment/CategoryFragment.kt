package com.zedorff.kotlin.ui.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.zedorff.kotlin.MainActivity
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.Category
import com.zedorff.kotlin.model.CategoryDao
import com.zedorff.kotlin.ui.adapter.CategoryAdapter
import com.zedorff.kotlin.ui.recycler.VerticalSpacingDecorator
import kotlinx.android.synthetic.main.category_list_fragment.view.*

const val CATEGORY_ID: String = "category_id"


class CategoryFragment: Fragment {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAddCategoryFAB: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.category_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.category_list_recycler
        mAddCategoryFAB = view.fab_add_category

        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.addItemDecoration(VerticalSpacingDecorator(10))
        mRecyclerView.adapter = CategoryAdapter(CategoryDao.loadAllCategories(), View.OnClickListener {
            val fragment = SpentFragment()
            val bundle = Bundle()
            bundle.putLong(CATEGORY_ID, (it.tag as Category).id)
            fragment.arguments = bundle
            (activity as MainActivity).replaceFragment(fragment)
        })
        CategoryDao.createCategory("test")
        mRecyclerView.adapter.notifyDataSetChanged()

        mAddCategoryFAB.setOnClickListener{
            MaterialDialog.Builder(activity)
                    .title("test")
                    .content("test content")
                    .positiveText("Добавить")
                    .negativeText("Отмена")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("HINTA", "PREFIL", { materialDialog: MaterialDialog, charSequence: CharSequence ->
                        CategoryDao.createCategory(charSequence.toString())
                        mRecyclerView.adapter.notifyDataSetChanged()
                    })
                    .show()
        }
    }

    constructor()
}