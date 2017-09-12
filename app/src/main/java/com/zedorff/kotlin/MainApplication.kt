package com.zedorff.kotlin

import android.app.Application
import com.zedorff.kotlin.model.Category
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().initialData( { realm ->
            val categories: MutableList<Category> = mutableListOf()
                resources.getStringArray(R.array.categories_outcome).forEach {
                    categories.add(Category(it, Category.OUTCOME))
                }
                resources.getStringArray(R.array.categories_income).forEach {
                    categories.add(Category(it, Category.INCOME))
                }
            realm.copyToRealm(categories)
        }).deleteRealmIfMigrationNeeded().build())
    }
}