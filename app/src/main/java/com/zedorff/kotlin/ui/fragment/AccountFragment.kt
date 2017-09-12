package com.zedorff.kotlin.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.getbase.floatingactionbutton.FloatingActionButton
import com.zedorff.kotlin.R
import com.zedorff.kotlin.daoWrappers.AccountDao
import com.zedorff.kotlin.ui.DialogCreator
import com.zedorff.kotlin.ui.adapter.AccountAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account_list.view.*

class AccountFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mNewAccountButton: FloatingActionButton
    private var accountDao: AccountDao = AccountDao()
    private var dialogCreator = DialogCreator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_account_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.account_list_recycler
        mNewAccountButton = view.fab_new_account
        mNewAccountButton.setOnClickListener {
            dialogCreator.createNewAccount(context)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mRecyclerView.adapter.notifyDataSetChanged()
                    })
        }
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        accountDao.getAllAccounts()
                .subscribe({ list ->
                    mRecyclerView.adapter = AccountAdapter(list)
                })
    }
}