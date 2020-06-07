package com.softwareforgood.pridefestival.util

import androidx.appcompat.widget.SearchView
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Common function used through all presenters to take only the latest search text and
 * make it lower case for passing to filtering methods.
 */
fun Observable<SearchViewQueryTextEvent>.toSearchableText(): Observable<String> =
        debounce(1, TimeUnit.SECONDS)
        .distinctUntilChanged()
        .map { it.queryText() }
        .map { it.toString() }
        .doOnNext { Timber.d("Search query of [%s]", it) }

fun Single<SearchView>.toSearchEventStream(): Observable<SearchViewQueryTextEvent> =
        flatMapObservable { it.eventStream }

val SearchView.eventStream get() = RxSearchView.queryTextChangeEvents(this)
