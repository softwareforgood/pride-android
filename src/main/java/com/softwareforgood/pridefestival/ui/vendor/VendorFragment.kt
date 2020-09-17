package com.softwareforgood.pridefestival.ui.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.databinding.ViewVendorBinding
import com.softwareforgood.pridefestival.ui.misc.SearchFragment
import io.reactivex.Observable

class VendorFragment : SearchFragment<ViewVendorBinding>() {

    override val toolbar: Toolbar get() = binding.toolbar.root
    override val title = "Vendors"

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): ViewVendorBinding {
        return ViewVendorBinding.inflate(layoutInflater, container, false)
    }

    override fun searchEvents(searchEvents: Observable<SearchViewQueryTextEvent>) {
        binding.root.presenter.search(searchEvents)
    }
}