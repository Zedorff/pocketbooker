package com.zedorff.kotlin.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.pawegio.kandroid.visible
import com.zedorff.kotlin.R
import com.zedorff.kotlin.daoWrappers.AccountDao
import com.zedorff.kotlin.daoWrappers.CategoryDao
import com.zedorff.kotlin.daoWrappers.TransactionDao
import com.zedorff.kotlin.model.Transaction
import com.zedorff.kotlin.ui.DialogCreator
import com.zedorff.kotlin.ui.adapter.TransactionAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_transaction_list.view.*


class TransactionFragment : Fragment(), View.OnClickListener {

    enum class TransactionType {
        INCOME,
        OUTCOME,
        TRANSFER
    }


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mFloatingMenu: FloatingActionsMenu
    private lateinit var mNewIncomeButton: FloatingActionButton
    private lateinit var mNewOutcomeButton: FloatingActionButton
    private lateinit var mNewTransferButton: FloatingActionButton
    private lateinit var mRecyclerAdapter: TransactionAdapter
    private var mTransactionList: MutableList<Transaction> = mutableListOf()
    private var mCategoryId: String? = null
    private var mTransactionDao = TransactionDao()
    private var mCategoryDao = CategoryDao()
    private var mAccountDao = AccountDao()
    private var mDialogCreator = DialogCreator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCategoryId = it.getString(CATEGORY_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.transaction_list_recycler
        mFloatingMenu = view.fab_new_transaction
        mNewIncomeButton = view.fab_new_income
        mNewOutcomeButton = view.fab_new_outcome
        mNewTransferButton = view.fab_new_transfer

        val manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        manager.reverseLayout = true

        mRecyclerView.layoutManager = manager
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && mFloatingMenu.isShown) {
                    mFloatingMenu.collapse()
                }
            }
        })

        mNewOutcomeButton.setOnClickListener(this)
        mNewIncomeButton.setOnClickListener(this)
        mNewTransferButton.setOnClickListener(this)


        mFloatingMenu.setOnFloatingActionsMenuUpdateListener(object : FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuCollapsed() {
                view.transaction_overlay.visible = false
            }

            override fun onMenuExpanded() {
                view.transaction_overlay.visible = true
            }
        })

        getCurrentTransactions()
                .subscribe { transactions ->
                    mTransactionList = transactions
                    mRecyclerAdapter = TransactionAdapter(mTransactionList)
                    mRecyclerView.adapter = mRecyclerAdapter
                }
    }

    override fun onClick(view: View) {
        when (view) {
            mNewIncomeButton -> {
                showActionDialog(TransactionType.INCOME)
            }
            mNewOutcomeButton -> {
                showActionDialog(TransactionType.OUTCOME)
            }
            mNewTransferButton -> {
                showActionDialog(TransactionType.TRANSFER)
            }
        }
    }

    private fun showActionDialog(type: TransactionType) {
        mAccountDao.getAllAccounts()
                .subscribe({ accounts ->
                    when (type) {
                        TransactionType.INCOME,TransactionType.OUTCOME -> {
                            if (accounts.size > 0) {
                                mDialogCreator.createNewTransaction(activity, type)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ transaction ->
                                            mTransactionList.add(transaction)
                                            mRecyclerAdapter.notifyItemInserted(0)
                                            mFloatingMenu.collapse()
                                        })
                            } else {
                                showNewAccountDialog(context, type)
                            }
                        }
                        TransactionType.TRANSFER -> {
                            if (accounts.size > 1) {
                                mDialogCreator.createNewTransfer(activity)
                                        .subscribe({ transaction ->
                                            mTransactionList.add(transaction)
                                            mRecyclerAdapter.notifyItemInserted(0)
                                            mFloatingMenu.collapse()
                                        })
                            } else {
                                showNewAccountDialog(context, type)
                            }
                        }
                    }
                })
    }

    private fun showNewAccountDialog(context: Context, type: TransactionType) {
        mDialogCreator.createNewAccount(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFloatingMenu.collapse()
                    showActionDialog(type)
                })
    }

    private fun getCurrentTransactions(): Observable<MutableList<Transaction>> =
            if (mCategoryId == null) {
                mTransactionDao.getAll()
            } else {
                mTransactionDao.getAllForCategoryID(mCategoryId!!)
            }

}