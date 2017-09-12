package com.zedorff.kotlin.daoWrappers

import com.pawegio.kandroid.w
import com.zedorff.kotlin.model.Category
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class CategoryDao {

    fun createCategory(name : String, type: Int) : Observable<Category> =
        Observable.create<Category> { obs ->
            Realm.getDefaultInstance().executeTransaction({
                val category = Category(name, type)
                it.copyToRealm(category)
                obs.onNext(category)
                obs.onComplete()
            })
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    fun getAll(): Observable<MutableList<Category>> =
            Observable.just(Realm.getDefaultInstance()
                    .where(Category::class.java)
                    .findAll().toMutableList())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllIncome(): Observable<MutableList<Category>> =
            Observable.just(Realm.getDefaultInstance()
                    .where(Category::class.java)
                    .equalTo(Category.TYPE, Category.INCOME)
                    .findAll().toMutableList())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllOutcome(): Observable<MutableList<Category>> =
            Observable.just(Realm.getDefaultInstance()
                    .where(Category::class.java)
                    .equalTo(Category.TYPE, Category.OUTCOME)
                    .findAll().toMutableList())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getCategoryByName(name: String): Observable<Category> =
            Observable.just(Realm.getDefaultInstance()
                    .where(Category::class.java)
                    .equalTo(Category.NAME, name)
                    .findFirst())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getCategoryById(id: String): Observable<Category> =
            Observable.just(Realm.getDefaultInstance()
                    .where(Category::class.java)
                    .equalTo(Category.ID, id)
                    .findFirst())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getTransferCategory(name: String, categoryType: Int): Observable<Category> =
            Observable.just(Realm.getDefaultInstance()
                    .where(Category::class.java)
                    .equalTo(Category.NAME, name)
                    .equalTo(Category.TYPE, categoryType)
                    .findFirst())
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}