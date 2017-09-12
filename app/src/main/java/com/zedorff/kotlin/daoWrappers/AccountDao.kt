package com.zedorff.kotlin.daoWrappers

import android.util.Log
import com.zedorff.kotlin.model.Account
import com.zedorff.kotlin.model.Category
import com.zedorff.kotlin.model.Transaction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.*

class AccountDao {

    fun createAccount(name: String, type: Int): Observable<Account> =
            Observable.create<Account> { obs ->
                Realm.getDefaultInstance().executeTransaction { realm ->
                    val account = Account(name, type)
                    realm.copyToRealm(account)
                    obs.onNext(account)
                    obs.onComplete()
                }
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun appendTransaction(account: Account, transaction: Transaction): Transaction {
        Realm.getDefaultInstance().executeTransaction {
            account.transactions.add(transaction)
            it.copyToRealmOrUpdate(account)
        }
        return transaction
    }

    fun getAccount(name: String): Observable<Account> =
            Observable.just( Realm.getDefaultInstance().where(Account::class.java)
                    .equalTo(Account.NAME, name)
                    .findFirst())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAccount(type: Int): Observable<Account> =
            Observable.just( Realm.getDefaultInstance().where(Account::class.java)
                    .equalTo(Account.TYPE, type)
                    .findFirst())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllAccounts(): Observable<MutableList<Account>> = Observable.just(
            Realm.getDefaultInstance()
                    .where(Account::class.java)
                    .findAll().toMutableList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

