package com.softwareforgood.pridefestival.util

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T : Any> Observable<T>.observeOnAndroidScheduler(): Observable<T> =
        this.observeOn(AndroidSchedulers.mainThread())

fun <T : Any> Single<T>.observeOnAndroidScheduler(): Single<T> =
        this.observeOn(AndroidSchedulers.mainThread())

fun <T : Any> Maybe<T>.observeOnAndroidScheduler(): Maybe<T> =
        this.observeOn(AndroidSchedulers.mainThread())

fun Completable.observeOnAndroidScheduler(): Completable =
        this.observeOn(AndroidSchedulers.mainThread())

fun Completable.subscribeOnIoScheduler(): Completable = subscribeOn(Schedulers.io())
fun <T : Any> Single<T>.subscribeOnIoScheduler(): Single<T> = subscribeOn(Schedulers.io())
fun <T : Any> Observable<T>.subscribeOnIoScheduler(): Observable<T> = subscribeOn(Schedulers.io())

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}
