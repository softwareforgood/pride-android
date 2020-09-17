package com.softwareforgood.pridefestival.ui.mvp

import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import io.reactivex.Observable

abstract class Presenter<VIEW> {
    private var _view: VIEW? = null
    val view: VIEW
        get() = _view ?: throw ViewNotAttachedException()

    val isViewAttached: Boolean
        get() = _view != null

    abstract fun onViewAttached()
    abstract fun onViewDetached()

    fun attachView(view: VIEW) {
        this._view = view
        onViewAttached()
    }

    fun detachView() {
        onViewDetached()
        this._view = null
    }

    open fun search(searches: Observable<SearchViewQueryTextEvent>) { }
}
