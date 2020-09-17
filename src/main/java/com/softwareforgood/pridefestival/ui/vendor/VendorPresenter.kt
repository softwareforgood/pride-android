package com.softwareforgood.pridefestival.ui.vendor

import androidx.annotation.VisibleForTesting
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.data.VendorLoader
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.ui.mvp.Presenter
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toSearchableText
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

abstract class VendorPresenter : Presenter<VendorView>()

@VendorScope
class DefaultVendorPresenter @Inject constructor(
    private val vendorLoader: VendorLoader,
    private val vendorAdapter: VendorAdapter,
    private val listDecor: StickyHeaderDecoration
) : VendorPresenter() {

    @VisibleForTesting
    var vendorDisposable = CompositeDisposable()

    override fun search(searches: Observable<SearchViewQueryTextEvent>) {
        vendorDisposable += searches
            .toSearchableText()
            .subscribe(::loadVendors)
    }

    override fun onViewAttached() {
        Timber.d("onViewAttached() called")

        with(view.recyclerView) {
            adapter = vendorAdapter
            addItemDecoration(listDecor)
        }

        loadVendors()

        view.tryAgainButton.setOnClickListener {
            view.showSpinner()
            loadVendors()
        }
    }

    override fun onViewDetached() {
        Timber.d("onViewDetached() called")
        vendorDisposable.clear()

        with(view.recyclerView) {
            adapter = null
            removeItemDecoration(listDecor)
        }
    }

    private fun loadVendors(searchParam: String = "") {
        vendorDisposable += vendorLoader.vendors
                .map { if (searchParam.isBlank()) it else it.filter { it.name.contains(searchParam, ignoreCase = true) } }
                .map { list -> list.sortedBy { it.name } }
                .observeOnAndroidScheduler()
                .subscribe(::showVendors, ::handleError)
    }

    private fun showVendors(vendors: List<Vendor>) {
        Timber.d("showVendors() called with paradeEvents = [%s]", vendors)
        vendorAdapter.loadVendors(vendors)
        view.showVendorList()
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "Error loading vendors")
        view.showError()
    }
}
