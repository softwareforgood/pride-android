package com.softwareforgood.pridefestival.ui.mvp

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
}
