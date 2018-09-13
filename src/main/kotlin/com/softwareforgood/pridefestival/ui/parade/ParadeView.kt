package com.softwareforgood.pridefestival.ui.parade

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.andrewreitz.velcro.betterviewanimator.BetterViewAnimator
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.util.activityComponent
import com.softwareforgood.pridefestival.util.horizontalDivider
import com.softwareforgood.pridefestival.util.toSearchEventStream
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.android.synthetic.main.view_parade.view.*
import javax.inject.Inject

interface ParadeView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    val searches: Observable<SearchViewQueryTextEvent>
    fun showParadeList()
    fun showError()
    fun showSpinner()
}

class DefaultParadeView(context: Context, attrs: AttributeSet)
    : BetterViewAnimator(context, attrs), ParadeView {

    override val recyclerView: RecyclerView get() = parade_list
    override val tryAgainButton: View get() = parade_error
    override val searches: Observable<SearchViewQueryTextEvent>
        get() = searchViewProvider.toSearchEventStream()

    @Inject lateinit var presenter: ParadePresenter
    @Inject lateinit var searchViewProvider: Single<SearchView>

    init {
        context.activityComponent.paradeComponent.inject(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        parade_list.horizontalDivider()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
    }

    override fun showParadeList() {
        displayedChildId = R.id.parade_list
    }

    override fun showError() {
        displayedChildId = R.id.parade_error
    }

    override fun showSpinner() {
        displayedChildId = R.id.parade_spinner
    }
}
