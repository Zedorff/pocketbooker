package com.zedorff.kotlin.model

import com.activeandroid.query.Select
import java.util.*

object SpentItemDao {

    fun createItem(name: String, cost: Float, category: Category) : SpentItem {
        var spentItem = SpentItem(name, Date(), cost, category)
        spentItem.save()
        return spentItem
    }

    fun loadAllItems(): List<SpentItem> = Select().from(SpentItem::class.java).execute<SpentItem>()
}