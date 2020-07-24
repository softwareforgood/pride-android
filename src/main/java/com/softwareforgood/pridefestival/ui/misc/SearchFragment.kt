package com.softwareforgood.pridefestival.ui.misc

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.util.*
import io.reactivex.Observable

abstract class SearchFragment<T : ViewBinding> : Fragment() {
    protected lateinit var binding: T

    abstract val toolbar: Toolbar
    abstract val title: String

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun searchEvents(searchEvents: Observable<SearchViewQueryTextEvent>)

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate(layoutInflater, container)
        setSupportActionBar(toolbar)
        setDisplayHomeAsUpEnabled(false)
        setDisplayShowHomeEnabled(false)
        toolBarTitle = title
        setHasOptionsMenu(true)

        return binding.root
    }

    final override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_main, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        searchEvents(searchView.eventStream)
    }
}
