package com.softwareforgood.pridefestival.ui.events

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.andrewreitz.velcro.betterviewanimator.BetterViewAnimator
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.util.activityComponent
import com.softwareforgood.pridefestival.util.horizontalDivider
import com.softwareforgood.pridefestival.util.toSearchEventStream
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.android.synthetic.main.view_events.view.*
import javax.inject.Inject

interface EventsView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    val searches: Observable<SearchViewQueryTextEvent>
    fun showEventsList()
    fun showError()
    fun showSpinner()
}

class DefaultEventsView(context: Context, attrs: AttributeSet) :
        BetterViewAnimator(context, attrs), EventsView {

    override val recyclerView: RecyclerView get() = events_list
    override val tryAgainButton: View get() = events_error
    override val searches: Observable<SearchViewQueryTextEvent> get() = searchViewProvider
            .toSearchEventStream()
            .skip(1) // skip the initial empty event

    @Inject lateinit var presenter: EventsPresenter
    @Inject lateinit var searchViewProvider: Single<SearchView>

    init {
        context.activityComponent.eventsComponent.inject(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        events_list.horizontalDivider()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
    }

    override fun showEventsList() {
        displayedChildId = events_list.id
    }

    override fun showError() {
        displayedChildId = events_error.id
    }

    override fun showSpinner() {
        displayedChildId = events_spinner.id
    }
}
