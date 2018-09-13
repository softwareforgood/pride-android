package com.softwareforgood.pridefestival.ui.favorites

import androidx.annotation.VisibleForTesting
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.softwareforgood.pridefestival.ui.mvp.Presenter
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toSearchableText
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@FavoritesScope
class FavoritesPresenter @Inject constructor(
    private val favoritesAdapter: FavoritesAdapter,
    private val listDecor: StickyHeaderDecoration
) : Presenter<FavoritesView>() {

    @VisibleForTesting
    var favoritesDisposable = CompositeDisposable()

    override fun onViewAttached() {
        Timber.d("onViewAttached() called")

        with(view.recyclerView) {
            adapter = favoritesAdapter
            addItemDecoration(listDecor)
        }

        favoritesDisposable += view.searches
                .toSearchableText()
                .observeOnAndroidScheduler()
                .subscribe(::loadFavorites)

        loadFavorites()

        view.tryAgainButton.setOnClickListener {
            view.showSpinner()
            loadFavorites()
        }
    }

    override fun onViewDetached() {
        Timber.d("onViewDetached() called")
        favoritesDisposable.clear()

        with(view.recyclerView) {
            adapter = null
            removeItemDecoration(listDecor)
        }
    }

    private fun loadFavorites(searchParam: String = "") {
        favoritesDisposable += favoritesAdapter.loadData(searchParam)
                .observeOnAndroidScheduler()
                .subscribe({ handleLoaded(searchParam) }, ::showError)

        favoritesAdapter.itemCount
    }

    private fun handleLoaded(searchParam: String) {
        if (searchParam.isBlank() && favoritesAdapter.itemCount == 0) {
            view.showEmptyMessage()
            return
        }
        view.showFavoritesList()
    }

    private fun showError(throwable: Throwable) {
        Timber.e(throwable, "An error occurred loading favorites.")
        view.showError()
    }
}
