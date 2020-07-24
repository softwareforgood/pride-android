package com.softwareforgood.pridefestival.ui.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.databinding.ViewEventsBinding
import com.softwareforgood.pridefestival.ui.misc.SearchFragment
import io.reactivex.Observable

class EventsFragment : SearchFragment<ViewEventsBinding>() {

    override val title = "Schedule"
    override val toolbar: Toolbar get() = binding.toolbar.root

    override fun searchEvents(searchEvents: Observable<SearchViewQueryTextEvent>) {
        binding.root.presenter.search(searchEvents)
    }
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): ViewEventsBinding {
        return ViewEventsBinding.inflate(layoutInflater, container, false)
    }
}
