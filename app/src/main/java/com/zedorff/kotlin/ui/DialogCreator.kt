package com.zedorff.kotlin.ui

import android.content.Context
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.zedorff.kotlin.R
import com.zedorff.kotlin.daoWrappers.AccountDao
import com.zedorff.kotlin.daoWrappers.CategoryDao
import com.zedorff.kotlin.daoWrappers.TransactionDao
import com.zedorff.kotlin.model.Account
import com.zedorff.kotlin.model.Category
import com.zedorff.kotlin.model.Transaction
import com.zedorff.kotlin.ui.fragment.TransactionFragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.dialog_add_account.view.*
import kotlinx.android.synthetic.main.dialog_add_transaction.view.*
import kotlinx.android.synthetic.main.dialog_new_transfer.view.*
import java.util.*

class DialogCreator {

    private val accountDao = AccountDao()
    private val transactionDao = TransactionDao()
    private val categoryDao = CategoryDao()

    fun createNewTransaction(context: Context, type: TransactionFragment.TransactionType): Observable<Transaction> =
            Observable.create<Transaction> { sub ->
                val dialog: MaterialDialog = MaterialDialog.Builder(context)
                        .iconRes(if (type ==TransactionFragment.TransactionType.OUTCOME) R.drawable.ic_outcome_dark else R.drawable.ic_income_dark)
                        .title(if (type == TransactionFragment.TransactionType.OUTCOME) R.string.dialog_transaction_title_outcome else R.string.dialog_transaction_title_income)
                        .titleColorRes(if (type == TransactionFragment.TransactionType.OUTCOME) android.R.color.holo_red_dark else android.R.color.holo_green_light)
                        .customView(R.layout.dialog_add_transaction, true)
                        .positiveText(R.string.dialog_add_text)
                        .negativeText(R.string.dialog_negative_text)
                        .onPositive({ materialDialog: MaterialDialog, _: DialogAction ->
                            with(materialDialog.customView!!) {
                                val transactionDescription = transaction_dialog_description.text.toString()
                                val transactionCost = transaction_dialog_cost.text.toString().toFloat()
                                val accountObservable = accountDao.getAccount(transaction_dialog_account_spinner.selectedItem.toString())
                                val categoryObservable = categoryDao.getCategoryByName(transaction_dialog_category_spinner.selectedItem.toString())
                                Observable.zip(accountObservable, categoryObservable,
                                        BiFunction<Account, Category, Transaction> { account: Account, category: Category ->
                                            return@BiFunction accountDao.appendTransaction(account,
                                                    transactionDao.createItem(transactionDescription,
                                                            transactionCost, category, Date().time, account))
                                        })
                                        .onErrorResumeNext(Observable.empty())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ trans ->
                                            sub.onNext(trans)
                                            sub.onComplete()
                                        })
                            }
                        }).build()

                with(dialog.customView!!) {
                    val categoryObservable = if (type == TransactionFragment.TransactionType.OUTCOME) categoryDao.getAllOutcome() else categoryDao.getAllIncome()
                    val accountObservable = accountDao.getAllAccounts()
                    categoryObservable.flatMapIterable { it }
                            .map { it.name }
                            .toList()
                            .subscribe({ categories ->
                                val categoryAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, categories)
                                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                transaction_dialog_category_spinner.adapter = categoryAdapter
                            })

                    accountObservable.flatMapIterable { it }
                            .map { it.name }
                            .toList()
                            .subscribe({ accounts ->
                                val accountAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, accounts)
                                accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                transaction_dialog_account_spinner.adapter = accountAdapter
                            })
                }
                dialog.show()
            }.observeOn(AndroidSchedulers.mainThread())

    fun createNewAccount(context: Context): Completable =
            Completable.create { sub ->
                val dialog: MaterialDialog = MaterialDialog.Builder(context)
                        .customView(R.layout.dialog_add_account, true)
                        .iconRes(R.drawable.ic_account)
                        .title(R.string.dialog_account_title)
                        .positiveText(R.string.dialog_add_text)
                        .negativeText(R.string.dialog_negative_text)
                        .onPositive({ materialDialog: MaterialDialog, _: DialogAction ->
                            with(materialDialog.customView!!) {
                                accountDao.createAccount(account_dialog_name.text.toString(),
                                        account_dialog_type_spinner.selectedItemPosition)
                                        .subscribe({
                                            sub.onComplete()
                                        })
                            }
                        })
                        .build()

                with(dialog.customView!!) {
                    val typeAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.accounts))
                    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    account_dialog_type_spinner.adapter = typeAdapter
                }
                dialog.show()
            }.observeOn(AndroidSchedulers.mainThread())


    fun createNewTransfer(context: Context): Observable<Transaction> =
        Observable.create<Transaction> { sub ->
            val dialog: MaterialDialog = MaterialDialog.Builder(context)
                    .customView(R.layout.dialog_new_transfer, true)
                    .iconRes(R.drawable.ic_transfer)
                    .title(R.string.dialog_transfer_title)
                    .positiveText(R.string.dialog_add_text)
                    .negativeText(R.string.dialog_negative_text)
                    .onPositive({ materialDialog: MaterialDialog, _: DialogAction ->
                        with(materialDialog.customView!!) {
                            Realm.getDefaultInstance().executeTransaction {
                                val fromAccount = accountDao.getAccount(transfer_dialog_from_account.selectedItem.toString())
                                val toAccount = accountDao.getAccount(transfer_dialog_to_account.selectedItem.toString())
                                val fromCategory = categoryDao.getTransferCategory(context.resources.getString(R.string.category_transfer_out), Category.OUTCOME)
                                val toCategory = categoryDao.getTransferCategory(context.resources.getString(R.string.category_transfer_in), Category.INCOME)
                                val time = Date().time
                                Observable.zip(fromAccount, toAccount, fromCategory, toCategory,
                                        Function4 { from: Account, to: Account, fromCat: Category, toCat: Category ->
                                            sub.onNext(accountDao.appendTransaction(from,
                                                    transactionDao.createItem(context.resources.getString(R.string.dialog_transfer_title),
                                                            transfer_dialog_cost.text.toString().toFloat(),
                                                            fromCat, time, from)))
                                            sub.onNext(accountDao.appendTransaction(to,
                                                    transactionDao.createItem(context.resources.getString(R.string.dialog_transfer_title),
                                                            transfer_dialog_cost.text.toString().toFloat(),
                                                            toCat, time, to)))
                                            sub.onComplete()
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe()
                            }
                        }
                    }).build()

            with(dialog.customView!!) {
                accountDao.getAllAccounts()
                        .flatMap { Observable.fromIterable(it) }
                        .map { it.name }
                        .toList()
                        .subscribe({ names ->
                            val typeAdapter = ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item, names)
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            transfer_dialog_from_account.adapter = typeAdapter
                            transfer_dialog_to_account.adapter = typeAdapter
                        })
            }
            dialog.show()
        }.observeOn(AndroidSchedulers.mainThread())
}