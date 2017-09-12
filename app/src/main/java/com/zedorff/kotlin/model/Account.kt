package com.zedorff.kotlin.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Account: RealmObject {

    companion object {
        var NAME = "name"
        var TYPE = "type"

        var CASH = 0
        var CARD = 1
        var SAVINGS = 2
    }

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    lateinit var name: String
    var type: Int = 0
    var transactions: RealmList<Transaction> = RealmList()

    constructor(name: String, type: Int) {
        this.name = name
        this.type = type
    }
    constructor() : super()
}