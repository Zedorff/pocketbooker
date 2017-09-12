package com.zedorff.kotlin.daoWrappers

import com.zedorff.kotlin.model.Account
import com.zedorff.kotlin.model.Category
import com.zedorff.kotlin.model.Transaction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort

class TransactionDao {

    val realm: Realm = Realm.getDefaultInstance()

    fun createItem(name: String, cost: Float, category: Category, date: Long, account: Account): Transaction =
            Transaction(name, date, category, if (category.type == Category.OUTCOME) -cost else cost, account)

    fun getAll(): Observable<MutableList<Transaction>> =
            Observable.just(realm.copyFromRealm(realm
                    .where(Transaction::class.java)
                    .findAllSorted(Transaction.DATE, Sort.ASCENDING).toMutableList()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllForCategory(category: Category): Observable<MutableList<Transaction>> =
            Observable.just(realm.copyFromRealm(realm
                    .where(Transaction::class.java)
                    .equalTo(Transaction.CATEGORY, category.name)
                    .findAllSorted(Transaction.DATE, Sort.ASCENDING).toMutableList()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllForCategoryID(id: String): Observable<MutableList<Transaction>> =
            Observable.just(realm.copyFromRealm(realm
                    .where(Transaction::class.java)
                    .equalTo("category.id", id)
                    .findAllSorted(Transaction.DATE, Sort.ASCENDING).toMutableList()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    fun getAllForAccount(account: Account): Observable<MutableList<Transaction>> =
            Observable.just(realm.copyFromRealm(realm
                    .where(Transaction::class.java)
                    .equalTo(Transaction.ACCOUNT, account.name)
                    .findAllSorted(Transaction.DATE, Sort.ASCENDING).toMutableList()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}