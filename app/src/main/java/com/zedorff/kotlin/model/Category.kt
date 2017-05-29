package com.zedorff.kotlin.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column


class Category : Model {

    @Column(name = "Name")
    var name: String? = null

    public fun getSpentItems(): List<SpentItem> = getMany(SpentItem::class.java, "Category")

    constructor(name : String) {
        this.name = name
    }

    constructor()
}