package com.softwareforgood.pridefestival.ui.favorites

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.databinding.ViewFavoritesBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.component
import com.softwareforgood.pridefestival.util.horizontalDivider
import javax.inject.Inject

interface FavoritesView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    fun showFavoritesList()
    fun showError()
    fun showSpinner()
    fun showEmptyMessage()
}

class DefaultFavoritesView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs), FavoritesView {

    override val recyclerView: RecyclerView get() = binding.list
    override val tryAgainButton: View get() = binding.error

    @Inject lateinit var presenter: FavoritesPresenter

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
        binding.viewAnimator.displayedChildId = binding.list.id
    }

    override fun showError() {
        binding.viewAnimator.displayedChildId = binding.error.id
    }

    override fun showSpinner() {
        binding.viewAnimator.displayedChildId = binding.spinner.id
    }

    override fun showEmptyMessage() {
        binding.viewAnimator.displayedChildId = binding.emptyMessage.id
    }
}
