package com.softwareforgood.pridefestival.ui.map

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.SupportMapFragment
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.ui.EasterEggActivity
import com.softwareforgood.pridefestival.util.activityComponent
import com.softwareforgood.pridefestival.util.horizontalDivider
import kotlinx.android.synthetic.main.view_map.view.*
import timber.log.Timber
import javax.inject.Inject

interface MapView {
    val locationListView: RecyclerView
    val backToLoringParkButton: Button

    fun goToEvent(event: Event)
    fun goToVendor(vendor: Vendor)
    fun showAndrew()
}

class DefaultMapView(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout(context, attrs), MapView {

    @Inject lateinit var fragmentManager: FragmentManager
    @Inject lateinit var presenter: MapPresenter
    @Inject lateinit var favoritesStorage: FavoritesStorage

    override val locationListView: RecyclerView get() = map_recycler_view
    override val backToLoringParkButton: Button get() = map_back_to_loring_park

    init {
        context.activityComponent.mapComponent.inject(this)
    }

    override fun onAttachedToWindow() {
        Timber.d("onAttachedToWindow() called")
        super.onAttachedToWindow()
        presenter.attachView(this)

        val mapFragment = fragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        Timber.d("found mapFragment = [%s]", mapFragment)
        mapFragment.getMapAsync(presenter::attachMap)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        map_recycler_view.horizontalDivider()
    }

    override fun onDetachedFromWindow() {
        Timber.d("onDetachedFromWindow() called")
        presenter.detachView()
        super.onDetachedFromWindow()
    }

    override fun goToEvent(event: Event) {
        Timber.d("goToEvent() called with event = [%s]", event)
        presenter.goToEvent(event)
    }

    override fun goToVendor(vendor: Vendor) {
        Timber.d("goToVendor() called with vendor = [%s]", vendor)
        presenter.goToVendor(vendor)
    }

    override fun showAndrew() {
        context.startActivity(Intent(context, EasterEggActivity::class.java))
    }
}
