package com.zedorff.kotlin.ui.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zedorff.kotlin.R
import com.zedorff.kotlin.model.Account
import kotlinx.android.synthetic.main.item_account.view.*

class AccountAdapter(private val mAccounts: MutableList<Account>) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AccountAdapter.ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_account, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = mAccounts.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val account = mAccounts[position]
        var amountOfMoney = 0f
        account.transactions.forEach {
            amountOfMoney += it.cost
        }
        val context = viewHolder.itemView.context
        val color = if (amountOfMoney > 0) context.resources.getColor(android.R.color.holo_blue_light)
                                            else context.resources.getColor(android.R.color.holo_red_light)
        viewHolder.name.text = account.name
        viewHolder.amount.text = "${amountOfMoney.toInt()} UAH"
        viewHolder.amount.setTextColor(color)
        viewHolder.icon.setImageResource(context.resources.obtainTypedArray(R.array.account_icons).getResourceId(position, -1))
    }

    open class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.account_name
        val amount: TextView = itemView.account_money_left
        val icon: ImageView = itemView.account_type_image
    }
}