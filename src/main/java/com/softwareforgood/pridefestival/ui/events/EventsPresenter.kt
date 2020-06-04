package com.softwareforgood.pridefestival.ui.events

import androidx.annotation.VisibleForTesting
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.softwareforgood.pridefestival.data.EventsLoader
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.ui.mvp.Presenter
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toSearchableText
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

abstract class EventsPresenter : Presenter<EventsView>()

@EventsScope
class DefaultEventsPresenter @Inject constructor(
    private val eventsLoader: EventsLoader,
    private val eventsAdapter: EventsAdapter,
    private val listDecor: StickyHeaderDecoration
) : EventsPresenter() {

    @VisibleForTesting
    var eventsDisposable = CompositeDisposable()

    override fun onViewAttached() {
        Timber.d("onViewAttached() called")

        with(view.recyclerView) {
            adapter = eventsAdapter
            addItemDecoration(listDecor)
        }

        eventsDisposable += view.searches
                .toSearchableText()
                .observeOnAndroidScheduler()
                .subscribe(::loadEvents)

        loadEvents()

        view.tryAgainButton.setOnClickListener {
            view.showSpinner()
            loadEvents()
        }
    }

    override fun onViewDetached() {
        Timber.d("onViewDetached() called")
        eventsDisposable.clear()

        with(view.recyclerView) {
            adapter = null
            removeItemDecoration(listDecor)
        }
    }

    private fun loadEvents(searchParam: String = "") {
        eventsDisposable += eventsLoader.events
                .map { if (searchParam.isBlank()) it else it.filter { it.name.contains(searchParam, ignoreCase = true) } }
                .map { it.sortedBy { it.name } }
                .map { it.sortedBy { it.startTime } }
                .observeOnAndroidScheduler()
                .subscribe(::showEvents, ::handleError)
    }

    private fun showEvents(events: List<Event>) {
        Timber.d("showEvents called() events = %s", events)
        eventsAdapter.loadEvents(events)
        view.showEventsList()
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "Error occurred")
        view.showError()
    }
}
