package com.softwareforgood.pridefestival.ui.misc

import android.app.Application
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.Vendor
import dagger.Reusable
import javax.inject.Inject

/**
 * Class to navigate to the map tab on the home screen.
 */
@Reusable
class MapNavigator @Inject constructor(private val app: Application) {

    /**
     * Navigates to the map tab of the application and shows the event's location
     * and information.
     */
    fun navigateToEvent(event: Event) {
        val p = NavDeepLinkBuilder(app)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.map_fragment)
            .setArguments(Bundle().apply { putString("event", event.objectId) })
            .createPendingIntent()

        p.send()
    }

    /**
     * Navigates to the map tab of the application and shows the vendor's location.
     */
    fun navigateToVendor(vendor: Vendor) {
        val p = NavDeepLinkBuilder(app)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.map_fragment)
            .setArguments(Bundle().apply { putString("vendor", vendor.objectId) })
            .createPendingIntent()

        p.send()
    }
}
