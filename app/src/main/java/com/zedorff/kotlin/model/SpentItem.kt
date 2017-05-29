package com.zedorff.kotlin.model

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import java.util.*

class SpentItem : Model {

    @Column(name = "Name")
    var name : String? = null

    @Column(name = "Date")
    var date : Date? = null

    @Column(name = "Category")
    var category : Category? = null

    @Column(name = "Cost")
    var cost : Float? = null

    constructor(name : String, date : Date, cost : Float, category: Category) {
        this.name = name
        this.date = date
        this.cost = cost
        this.category = category
    }

    constructor()
}