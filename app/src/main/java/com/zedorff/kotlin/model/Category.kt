package com.zedorff.kotlin.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class Category : RealmObject {

    companion object {
        var ID = "id"
        var NAME = "name"
        var TYPE = "type"

        var INCOME = 0
        var OUTCOME = 1
    }

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()

    lateinit var name: String
    var type: Int = 0

    constructor(name: String, type: Int) : super() {
        this.name = name
        this.type = type
    }
    constructor() : super()
}