package com.softwareforgood.pridefestival.ui.vendor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.databinding.ViewVendorBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.component
import com.softwareforgood.pridefestival.util.horizontalDivider
import javax.inject.Inject

interface VendorView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    fun showVendorList()
    fun showError()
    fun showSpinner()
}

class DefaultVendorView(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs), VendorView {

    override val recyclerView: RecyclerView get() = binding.list
    override val tryAgainButton: View get() = binding.error

    @Inject lateinit var presenter: VendorPresenter

    private lateinit var binding: ViewVendorBinding

    init {
        component<ActivityComponent>().vendorComponent.inject(this)
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
        binding.viewAnimator.displayedChildId = binding.list.id
    }

    override fun showError() {
        binding.viewAnimator.displayedChildId = binding.error.id
    }

    override fun showSpinner() {
        binding.viewAnimator.displayedChildId = binding.spinner.id
    }
}
