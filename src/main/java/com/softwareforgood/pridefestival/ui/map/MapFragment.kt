package com.softwareforgood.pridefestival.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softwareforgood.pridefestival.BreadCrumbManager
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.ui.ActivityComponent
import com.softwareforgood.pridefestival.util.component
import timber.log.Timber
import javax.inject.Inject

class MapFragment : Fragment() {

    @Inject lateinit var mapPresenter: MapPresenter
    @Inject lateinit var breadCrumbManager: BreadCrumbManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.view_map, container, false) as DefaultMapView

        val mapComponent = view.component<ActivityComponent>().mapComponent
        mapComponent.inject(view)
        mapComponent.inject(this)

        arguments?.getString("event")?.let { event ->
            Timber.d("onCreateView() arguments with event = [%s]", event)
            breadCrumbManager.logBreadCrumb("navigation to event from map, event = [$event]")
            mapPresenter.goToEvent(event)
        }

        arguments?.getString("vendor")?.let { vendor ->
            Timber.d("onCreateView() arguments with vendor = [%s]", vendor)
            breadCrumbManager.logBreadCrumb("navigation to vendor from map, vendor = [$vendor]")
            mapPresenter.goToVendor(vendor)
        }

        return view
    }
}
