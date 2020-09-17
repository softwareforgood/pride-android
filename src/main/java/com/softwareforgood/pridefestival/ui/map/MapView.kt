package com.softwareforgood.pridefestival.ui.map

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.maps.SupportMapFragment
import com.softwareforgood.pridefestival.BreadCrumbManager
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.databinding.ViewMapBinding
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.ui.EasterEggActivity
import com.softwareforgood.pridefestival.util.component
import com.softwareforgood.pridefestival.util.getFragmentByTag
import com.softwareforgood.pridefestival.util.horizontalDivider
import timber.log.Timber
import javax.inject.Inject

interface MapView {
    val locationListView: RecyclerView
    val backToLoringParkButton: Button

    fun showAndrew()
}

class DefaultMapView(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout(context, attrs), MapView {

    @Inject lateinit var presenter: MapPresenter
    @Inject lateinit var favoritesStorage: FavoritesStorage
    @Inject lateinit var breadCrumbManager: BreadCrumbManager

    override val locationListView: RecyclerView get() = binding.recyclerView
    override val backToLoringParkButton: Button get() = binding.backToLoringPark

    private lateinit var binding: ViewMapBinding

    override fun onAttachedToWindow() {
        Timber.d("onAttachedToWindow() called")
        breadCrumbManager.logBreadCrumb("Map attached to window")
        super.onAttachedToWindow()
        presenter.attachView(this)

        val mapFragment = binding.map.findFragment<MapFragment>()
        mapFragment.childFragmentManager
            .getFragmentByTag<SupportMapFragment>("google_map_fragment")
            .getMapAsync(presenter::attachMap)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewMapBinding.bind(this)
        binding.recyclerView.horizontalDivider()
    }

    override fun onDetachedFromWindow() {
        Timber.d("onDetachedFromWindow() called")
        presenter.detachView()
        super.onDetachedFromWindow()
    }

    override fun showAndrew() {
        breadCrumbManager.logBreadCrumb("going to easteregg")
        context.startActivity(Intent(context, EasterEggActivity::class.java))
    }
}
