package com.softwareforgood.pridefestival.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.softwareforgood.pridefestival.ui.mvp.Presenter
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.EventsLoader
import com.softwareforgood.pridefestival.data.VendorLoader
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.Mappable
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.ui.map.GeoCoordinates.LORING_PARK_MAP_BOUNDS
import com.softwareforgood.pridefestival.ui.map.GeoCoordinates.LORING_POND_BOUNDS
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toLatLng
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

private object GeoCoordinates {
    val PARK_NORTH_EAST = LatLng(44.972189, -93.28186)
    val PARK_SOUTH_WEST = LatLng(44.967810, -93.287307)
    val LORING_PARK_MAP_BOUNDS = LatLngBounds(PARK_SOUTH_WEST, PARK_NORTH_EAST)
    val LORING_POND_BOUNDS = LatLngBounds(LatLng(44.968659, -93.285092), LatLng(44.969494, -93.283718))
}

sealed class MapMarkerData<out T : Mappable> {
    abstract val data: T
}

data class EventMarkerData(override val data: Event) : MapMarkerData<Event>()
data class VendorMarkerData(override val data: Vendor) : MapMarkerData<Vendor>()

abstract class MapPresenter : Presenter<MapView>() {
    abstract fun attachMap(map: GoogleMap)
    abstract fun goToEvent(event: Event)
    abstract fun goToVendor(vendor: Vendor)
}

@MapScope
class DefaultMapPresenter @Inject constructor(
    private val activity: AppCompatActivity,
    private val eventsLoader: EventsLoader,
    private val vendorLoader: VendorLoader,
    private val mapDataPagerAdapter: MapDataPagerAdapter
) : MapPresenter() {

    companion object {
        /** Request code for EasyPermissions to keep track of permissions. */
        const val LOCATION_REQUEST_CODE = 4631

        /**
         * 21 largest zoom size
         * https://developers.google.com/android/reference/com/google/android/gms/maps/CameraUpdateFactory#newLatLngBounds(com.google.android.gms.maps.model.LatLngBounds, int)
         */
        private const val MAX_ZOOM_LEVEL = 21

        /**
         * The zoom level to zoom in on a Vendor or Event when a vendor or event has been
         * clicked on in their list.
         */
        private const val MAP_GO_TO_ZOOM_LEVEL = 19.5f

        private const val MAP_STYLE_OPTIONS_JSON = """
                [
                  {
                    "featureType": "poi",
                    "elementType": "all",
                    "stylers": [
                      {
                        "visibility": "off"
                      }
                    ]
                  }
                ]
            """
    }

    private var markers = emptySet<Marker>()
    private var markerData = emptySet<MapMarkerData<Mappable>>()

    private val disposables = CompositeDisposable()

    private var map: GoogleMap? = null

    private val loringCameraOverlay get() = GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.pride_map))
                .positionFromBounds(LORING_PARK_MAP_BOUNDS)

    private val loringCamerUpdate get() = CameraUpdateFactory.newLatLngBounds(
                LORING_PARK_MAP_BOUNDS,
                MAX_ZOOM_LEVEL
    )

    private var goToEvent: () -> Unit = {}
    private var goToVendor: () -> Unit = {}

    /** @see easterEggClickHandler */
    private var pondClickCount = AtomicInteger(0)

    override fun onViewAttached() {
        Timber.d("onViewAttached() called")
        view.locationListView.adapter = mapDataPagerAdapter
    }

    override fun onViewDetached() {
        Timber.d("onViewDetached() called")
        goToEvent = {}
        markers = emptySet()
        markerData = emptySet()
        map?.setOnCameraMoveStartedListener(null)
        map?.clear()
        view.locationListView.adapter = null
        view.locationListView.visibility = View.GONE
        disposables.clear()
    }

    override fun attachMap(map: GoogleMap) {
        Timber.d("attachMap() called map = [%s]", map)
        this.map = map

        handlePermissions()

        map.apply {
            uiSettings.isCompassEnabled = true
            setOnMarkerClickListener { marker -> markerClicked(map, marker) }
            setOnMapClickListener {
                hideMarkers()
                easterEggClickHandler(it)
            }
            setMapStyle(MapStyleOptions(MAP_STYLE_OPTIONS_JSON))
            addGroundOverlay(loringCameraOverlay)
            moveCamera(loringCamerUpdate)
            setOnCameraMoveListener {
                setMarkerIconBasedOnZoom()
                showOrHideBackToLoringParkButtonBasedOnMapLocation()
            }
        }

        disposables += vendorLoader.vendors
                .toObservable()
                .flatMapIterable { it }
                .filter { vendor -> vendor.location != null }
                .map { vendor ->
                    VendorMarkerData(vendor) to MarkerOptions().title(vendor.name)
                            .position(vendor.location!!.toLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.annotation_small))
                }
                .observeOnAndroidScheduler()
                .subscribe { (vendor, markerOptions) ->
                    val marker = map.addMarker(markerOptions)
                    marker.tag = vendor
                    markers += marker
                    markerData += vendor
                }

        disposables += eventsLoader.events
                .toObservable()
                .flatMapIterable { it }
                .filter { event -> event.location != null }
                .map { event ->
                    EventMarkerData(event) to MarkerOptions().title(event.name)
                            .position(event.location!!.toLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.annotation_small))
                }
                .observeOnAndroidScheduler()
                .doOnComplete {
                    // if we got here from clicking and event or vender,
                    // now that everything was loaded try to go there now.
                    goToEvent()
                    goToEvent = {}
                    goToVendor()
                    goToVendor = {}
                }
                .subscribe { (event, markerOptions) ->
                    val marker = map.addMarker(markerOptions)
                    marker.tag = event
                    markers += marker
                    markerData += event
                }

        // map loaded call back can be called after user has navigated away from the screen so
        // we need to check if the view is still attached before trying to attach a listener.
        if (isViewAttached) view.backToLoringParkButton.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(LORING_PARK_MAP_BOUNDS, MAX_ZOOM_LEVEL))
        }
    }

    override fun goToEvent(event: Event) {
        Timber.d("goToEvent() event = [%s]", event)
        goToEvent = {
            Timber.d("goToEvent\$lambda() event = [%s]", event)

            val data = EventMarkerData(event)
            val markers = markerData.filter { it.equalOnLocation(data) }.toMutableList()
            Timber.d("goToEvent() markers = [%s]", markers)

            // place the event that was passed in at the top of the list
            val firstMarker = markers.first { (it.data as? Event) == event }
            markers.remove(firstMarker)
            markers.add(0, firstMarker)

            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(event.location?.toLatLng(), MAP_GO_TO_ZOOM_LEVEL))

            showMarkers(markers)
        }
    }

    override fun goToVendor(vendor: Vendor) {
        Timber.d("goToVendor() vendor = [%s]", vendor)
        goToVendor = {
            Timber.d("goToVendor\$lambda() vendor = [%s]", vendor)

            val data = VendorMarkerData(vendor)
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(vendor.location?.toLatLng(), MAP_GO_TO_ZOOM_LEVEL))
            showMarkers(listOf(data))
        }
    }

    private fun setMarkerIconBasedOnZoom() {
        map != null || return
        val zoom = map!!.cameraPosition.zoom
            val icon = when {
                zoom >= 18f -> R.drawable.annotation
                else -> R.drawable.annotation_small
            }.let { BitmapDescriptorFactory.fromResource(it) }
            markers.forEach { it.setIcon(icon) }
    }

    private fun showOrHideBackToLoringParkButtonBasedOnMapLocation() {
        map != null || return
        val target = map!!.cameraPosition.target
        if (isViewAttached) view.backToLoringParkButton.visibility = when {
            LORING_PARK_MAP_BOUNDS.contains(target) -> View.INVISIBLE
            else -> View.VISIBLE
        }
    }

    @SuppressLint("MissingPermission")
    private fun handlePermissions() {
        if (EasyPermissions.hasPermissions(activity, ACCESS_FINE_LOCATION)) {
            map?.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(activity,
                    activity.getString(R.string.location_permission_request),
                    LOCATION_REQUEST_CODE,
                    ACCESS_FINE_LOCATION
            )
        }
    }

    private fun markerClicked(map: GoogleMap, marker: Marker): Boolean {
        val data = marker.tag as MapMarkerData<Mappable>
        val markers = markerData.filter { it.equalOnLocation(data) }

        map.animateCamera(CameraUpdateFactory.newLatLng(data.data.location?.toLatLng()))

        showMarkers(markers)
        return true
    }

    private fun showMarkers(markers: List<MapMarkerData<Mappable>>) {
        Timber.d("bindMarkerData() called with markerData = [%s]", markers)
        mapDataPagerAdapter.setData(markers)
        if (isViewAttached) view.locationListView.visibility = View.VISIBLE
    }

    private fun hideMarkers() {
        Timber.d("hideMarkerData() called")
        if (isViewAttached) view.locationListView.visibility = View.GONE
    }

    /** Check if clicked on lorning park text quickly to launch easter egg. */
    private fun easterEggClickHandler(clickLocation: LatLng) {
        if (!LORING_POND_BOUNDS.contains(clickLocation)) return
        if (pondClickCount.get() == 0) {
            disposables += Completable.timer(3, TimeUnit.SECONDS)
                    .subscribe {
                        Timber.d("Reseting easter egg timer...")
                        pondClickCount.set(0)
                    }
        }
        if (pondClickCount.incrementAndGet() == 3) view.showAndrew()
    }

    private fun MapMarkerData<Mappable>.equalOnLocation(other: MapMarkerData<Mappable>): Boolean {
        val a = data.location
        val b = other.data.location
        return a?.latitude == b?.latitude && a?.longitude == b?.longitude
    }
}
