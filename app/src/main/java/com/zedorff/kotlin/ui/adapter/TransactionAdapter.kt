package com.zedorff.kotlin.ui.adapter

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.Category
import com.zedorff.kotlin.model.Transaction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.item_transaction.view.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter() : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    lateinit private var mTransactionList: MutableList<Transaction>
    var multiSelect = false
    val selectedItems = ArrayList<Int>()
    val actionModeCallbacks: android.support.v7.view.ActionMode.Callback = object : android.support.v7.view.ActionMode.Callback {
        var isAllSelected: Boolean = false

        override fun onCreateActionMode(mode: android.support.v7.view.ActionMode, menu: Menu): Boolean {
            multiSelect = true
            mode.menuInflater.inflate(R.menu.transaction_multi_select_menu, menu)
            return true
        }

        override fun onPrepareActionMode(p0: android.support.v7.view.ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: android.support.v7.view.ActionMode, menu: MenuItem): Boolean {
            when (menu.itemId) {
                R.id.transaction_select_all -> {
                    if (isAllSelected || selectedItems.size == mTransactionList.size) {
                        selectedItems.clear()
                        notifyDataSetChanged()
                        mode.finish()
                        isAllSelected = false
                    } else {
                        mTransactionList.forEachIndexed { index, _ ->
                            if (!selectedItems.contains(index)) {
                                selectedItems.add(index)
                                notifyItemChanged(index)
                            }
                        }
                        isAllSelected = true
                    }
                }
                R.id.delete_transaction -> {
                    Observable.fromIterable(selectedItems.sortedWith(compareBy({-it})))
                            .doOnNext { index ->
                                Realm.getDefaultInstance().executeTransaction {
                                    it.where(Transaction::class.java)
                                            .equalTo(Transaction.ID, mTransactionList[index].id)
                                            .findFirst()
                                            .deleteFromRealm()
                                }
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ index ->
                                mTransactionList.removeAt(index)
                                notifyItemRemoved(index)
                            })
                    selectedItems.clear()
                    mode.finish()
                }
            }
            return true
        }

        override fun onDestroyActionMode(p0: android.support.v7.view.ActionMode?) {
            multiSelect = false
            selectedItems.clear()
            notifyDataSetChanged()
        }
    }

    constructor(transactionList: MutableList<Transaction>) : this() {
        setHasStableIds(true)
        mTransactionList = transactionList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_transaction, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val transactionItem = mTransactionList[position]
        viewHolder.mTransactionName.text = transactionItem.name
        viewHolder.mTransactionCategory.text = "${transactionItem.category.name} | ${transactionItem.account.name}"
        viewHolder.mTransactionCost.text = "${transactionItem.cost.toInt()} UAH"
        viewHolder.mTransactionCost.setTextColor(viewHolder.itemView.context.resources.getColor(if (transactionItem.category.type == Category.OUTCOME) android.R.color.holo_red_dark else android.R.color.holo_green_light))
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()
        viewHolder.mTransactionDate.text = simpleDateFormat.format(transactionItem.date).toString()
        viewHolder.update(position)
    }

    override fun getItemCount(): Int = mTransactionList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTransactionName: TextView = itemView.transaction_name
        var mTransactionCategory: TextView = itemView.transaction_category
        var mTransactionCost: TextView = itemView.transaction_cost
        var mTransactionDate: TextView = itemView.transaction_date

        private fun selectItem(item: Int) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                    itemView.isSelected = false
                } else {
                    selectedItems.add(item)
                    itemView.isSelected = true
                }
            }
            notifyItemChanged(item)
        }

        fun update(value: Int) {
            itemView.isSelected = selectedItems.contains(value)
            itemView.setOnLongClickListener { view ->
                if (!multiSelect) {
                    (view.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                    selectItem(value)
                }
                true
            }
            itemView.setOnClickListener { selectItem(value) }
        }
    }
}