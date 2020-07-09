package com.softwareforgood.pridefestival.ui.favorites

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.andrewreitz.velcro.betterviewanimator.BetterViewAnimator
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.databinding.ViewFavoritesBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.component
import com.softwareforgood.pridefestival.util.horizontalDivider
import com.softwareforgood.pridefestival.util.toSearchEventStream
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

interface FavoritesView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    val searches: Observable<SearchViewQueryTextEvent>
    fun showFavoritesList()
    fun showError()
    fun showSpinner()
    fun showEmptyMessage()
}

class DefaultFavoritesView(
    context: Context,
    attrs: AttributeSet
) : BetterViewAnimator(context, attrs), FavoritesView {

    override val recyclerView: RecyclerView get() = binding.list
    override val tryAgainButton: View get() = binding.error
    override val searches: Observable<SearchViewQueryTextEvent>
        get() = searchViewProvider.toSearchEventStream()

    @Inject lateinit var presenter: FavoritesPresenter
    @Inject lateinit var searchViewProvider: Single<SearchView>

    private lateinit var binding: ViewFavoritesBinding

    init {
        component<ActivityComponent>().favoritesComponent.inject(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewFavoritesBinding.bind(this)
        binding.list.horizontalDivider()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
    }

    override fun showFavoritesList() {
        displayedChildId = binding.list.id
    }

    override fun showError() {
        displayedChildId = binding.error.id
    }

    override fun showSpinner() {
        displayedChildId = binding.spinner.id
    }

    override fun showEmptyMessage() {
        displayedChildId = binding.emptyMessage.id
    }
}
