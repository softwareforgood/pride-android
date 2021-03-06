package com.softwareforgood.pridefestival.ui.parade

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.databinding.ViewParadeBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.component
import com.softwareforgood.pridefestival.util.horizontalDivider
import javax.inject.Inject

interface ParadeView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    fun showParadeList()
    fun showError()
    fun showSpinner()
}

class DefaultParadeView(context: Context, attrs: AttributeSet)
    : ConstraintLayout(context, attrs), ParadeView {

    override val recyclerView: RecyclerView get() = binding.list
    override val tryAgainButton: View get() = binding.error

    @Inject lateinit var presenter: ParadePresenter

    private lateinit var binding: ViewParadeBinding

    init {
        component<ActivityComponent>().paradeComponent.inject(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewParadeBinding.bind(this)
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

    override fun showParadeList() {
        binding.viewAnimator.displayedChildId = binding.list.id
    }

    override fun showError() {
        binding.viewAnimator.displayedChildId = binding.error.id
    }

    override fun showSpinner() {
        binding.viewAnimator.displayedChildId = binding.spinner.id
    }
}
