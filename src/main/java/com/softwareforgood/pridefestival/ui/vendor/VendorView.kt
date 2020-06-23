package com.softwareforgood.pridefestival.ui.vendor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.andrewreitz.velcro.betterviewanimator.BetterViewAnimator
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.softwareforgood.pridefestival.databinding.ViewVendorBinding
import com.softwareforgood.pridefestival.util.activityComponent
import com.softwareforgood.pridefestival.util.horizontalDivider
import com.softwareforgood.pridefestival.util.toSearchEventStream
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

interface VendorView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    val searches: Observable<SearchViewQueryTextEvent>
    fun showVendorList()
    fun showError()
    fun showSpinner()
}

class DefaultVendorView(context: Context, attrs: AttributeSet)
    : BetterViewAnimator(context, attrs), VendorView {

    override val recyclerView: RecyclerView get() = binding.list
    override val tryAgainButton: View get() = binding.error
    override val searches: Observable<SearchViewQueryTextEvent>
        get() = searchViewProvider.toSearchEventStream()

    @Inject lateinit var presenter: VendorPresenter
    @Inject lateinit var searchViewProvider: Single<SearchView>

    private lateinit var binding: ViewVendorBinding

    init {
        context.activityComponent.vendorComponent.inject(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewVendorBinding.bind(this)
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

    override fun showVendorList() {
        displayedChildId = binding.list.id
    }

    override fun showError() {
        displayedChildId = binding.error.id
    }

    override fun showSpinner() {
        displayedChildId = binding.spinner.id
    }
}
