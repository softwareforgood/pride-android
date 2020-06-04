package com.softwareforgood.pridefestival.ui.parade

import androidx.annotation.VisibleForTesting
import com.softwareforgood.pridefestival.data.ParadeLoader
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.ui.mvp.Presenter
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toSearchableText
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

abstract class ParadePresenter : Presenter<ParadeView>()

@ParadeScope
class DefaultParadePresenter @Inject constructor(
    private val paradeLoader: ParadeLoader,
    private val paradeAdapter: ParadeAdapter
) : ParadePresenter() {

    @VisibleForTesting
    var eventsDisposable = CompositeDisposable()

    override fun onViewAttached() {
        Timber.d("onViewAttached() called")

        view.recyclerView.adapter = paradeAdapter

        eventsDisposable += view.searches
                .toSearchableText()
                .subscribe(::loadParades)

        loadParades()

        view.tryAgainButton.setOnClickListener {
            view.showSpinner()
            loadParades()
        }
    }

    override fun onViewDetached() {
        Timber.d("onViewDetached() called")
        eventsDisposable.clear()
    }

    private fun loadParades(searchParam: String = "") {
        eventsDisposable += paradeLoader.parades
                .map { if (searchParam.isBlank()) it else it.filter { it.name.contains(searchParam, ignoreCase = true) } }
                .map { it.sortedBy { it.lineupNumber } }
                .observeOnAndroidScheduler()
                .subscribe(::showParadeEvents, ::handleError)
    }

    private fun showParadeEvents(paradeEvents: List<ParadeEvent>) {
        Timber.d("showParadeEvents() called with paradeEvents = [%s]", paradeEvents)
        paradeAdapter.loadParadeEvents(paradeEvents)
        view.showParadeList()
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "Error loading parades")
        view.showError()
    }
}
