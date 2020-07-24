package com.softwareforgood.pridefestival.ui.parade

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.databinding.ViewParadeBinding
import com.softwareforgood.pridefestival.ui.misc.SearchFragment
import io.reactivex.Observable

class ParadeFragment : SearchFragment<ViewParadeBinding>() {

    override val toolbar get() = binding.toolbar.root
    override val title = "Parade"

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): ViewParadeBinding {
        return ViewParadeBinding.inflate(layoutInflater, container, false)
    }

    override fun searchEvents(searchEvents: Observable<SearchViewQueryTextEvent>) {
        binding.root.presenter.search(searchEvents)
    }
}
