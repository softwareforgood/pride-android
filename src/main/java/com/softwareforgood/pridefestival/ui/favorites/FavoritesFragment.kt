package com.softwareforgood.pridefestival.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.databinding.ViewFavoritesBinding
import com.softwareforgood.pridefestival.ui.misc.SearchFragment
import io.reactivex.Observable

class FavoritesFragment : SearchFragment<ViewFavoritesBinding>() {
    override val toolbar: Toolbar get() = binding.toolbar.root
    override val title: String = "Favorites"

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): ViewFavoritesBinding {
        return ViewFavoritesBinding.inflate(layoutInflater, container, false)
    }

    override fun searchEvents(searchEvents: Observable<SearchViewQueryTextEvent>) {
        binding.root.presenter.search(searchEvents)
    }
}
