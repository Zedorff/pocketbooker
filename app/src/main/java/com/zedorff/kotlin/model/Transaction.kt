package com.zedorff.kotlin.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Transaction : RealmObject {

    companion object {
        var ID = "id"
        var NAME = "name"
        var CATEGORY = "category.name"
        var ACCOUNT = "account.name"
        var DATE = "date"
        var COST = "cost"
    }


    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    lateinit var name: String
    lateinit var category: Category
    lateinit var account: Account
    var cost: Float = 0f
    var date: Long = 0

    constructor(name: String, date: Long, category: Category, cost: Float, account: Account) : super() {
        this.name = name
        this.date = date
        this.category = category
        this.cost = cost
        this.account = account
    }

    constructor() : super()
}