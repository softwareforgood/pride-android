package com.softwareforgood.pridefestival.ui.events

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.databinding.ViewEventsBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.component
import com.softwareforgood.pridefestival.util.horizontalDivider
import javax.inject.Inject

interface EventsView {
    val recyclerView: RecyclerView
    val tryAgainButton: View
    fun showEventsList()
    fun showError()
    fun showSpinner()
}

class DefaultEventsView(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs), EventsView {

    override val recyclerView: RecyclerView get() = binding.list
    override val tryAgainButton: View get() = binding.error

    @Inject lateinit var presenter: EventsPresenter

    private lateinit var binding: ViewEventsBinding

    init {
        component<ActivityComponent>().eventsComponent.inject(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewEventsBinding.bind(this)
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

    override fun showEventsList() {
        binding.viewAnimator.displayedChildId = binding.list.id
    }

    override fun showError() {
        binding.viewAnimator.displayedChildId = binding.error.id
    }

    override fun showSpinner() {
        binding.viewAnimator.displayedChildId = binding.spinner.id
    }
}
