package com.softwareforgood.pridefestival.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.ui.info.InfoActivity
import com.softwareforgood.pridefestival.ui.map.MapView
import com.softwareforgood.pridefestival.ui.misc.EventClickHandler
import com.softwareforgood.pridefestival.ui.misc.VendorClickHandler
import com.softwareforgood.pridefestival.util.HasComponent
import com.softwareforgood.pridefestival.util.launchActivity
import com.softwareforgood.pridefestival.util.makeComponent
import com.softwareforgood.pridefestival.util.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

import kotlinx.android.synthetic.main.activity_main.*

import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasComponent<ActivityComponent> {

    @Inject lateinit var eventClickHandler: EventClickHandler
    @Inject lateinit var vendorClickHandler: VendorClickHandler

    /**
     * Map of Navigation Enum to it's corresponding inflated view.
     *
     * Must be by lazy to wait until the layout inflater is available.
     */
    private lateinit var viewCache: Map<Navigation, View>

    private val disposables = CompositeDisposable()
    private val searchViewPublisher: Subject<SearchView> = BehaviorSubject.create()

    override val component: ActivityComponent by lazy {
        makeComponent(searchViewPublisher.take(1).singleOrError())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate() called with saveInstantSate = [%s]", savedInstanceState)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        component.inject(this)

        setSupportActionBar(toolbar as Toolbar)

        viewCache = Navigation.values()
                .map { nav -> nav to nav.inflate() }
                .toMap()

        Navigation.MAP.navigate()
        setMapAsSelectedOnBottomNav()
        bottom_navigation.setOnNavigationItemSelectedListener(navigationHandler)
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.d("onCreateOptionsMenu() menu = [%s]", menu)
        menuInflater.inflate(R.menu.toolbar_main, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchViewPublisher.onNext(searchView)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_info -> {
            launchActivity(InfoActivity::class)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private val navigationHandler = { item: MenuItem ->
        Navigation.fromActionId(item.itemId).navigate()
        true
    }

    private fun Navigation.inflate() =
            layoutInflater.inflate(layout, main_activity_container, false)

    private fun Navigation.navigate() {
        Timber.i("navigate() on %s", this)
        disposables.clear()

        toolbar.visibility = if (this == Navigation.MAP) View.GONE else View.VISIBLE

        val view = viewCache[this]
        main_activity_container.apply {
            removeAllViews()
            addView(view)
        }

        if (this == Navigation.EVENTS) {
            disposables += eventClickHandler.eventClicks()
                    .subscribe(
                        {
                            setMapAsSelectedOnBottomNav()
                            val mapView = viewCache[Navigation.MAP]
                            main_activity_container.apply {
                                removeAllViews()
                                (mapView as MapView).goToEvent(it)
                                addView(mapView)
                            }
                        },
                        { Timber.e(it, "Error subscribing to event clicks") }
                    )
            return
        }

        if (this == Navigation.VENDOR) {
            disposables += vendorClickHandler.vendorClicks()
                    .subscribe(
                            {
                                setMapAsSelectedOnBottomNav()
                                val mapView = viewCache[Navigation.MAP]
                                main_activity_container.apply {
                                    removeAllViews()
                                    (mapView as MapView).goToVendor(it)
                                    addView(mapView)
                                }
                            },
                            { Timber.e(it, "Error subscribing to vendor clicks") }
                    )

            return
        }
    }

    private fun setMapAsSelectedOnBottomNav() {
        bottom_navigation.selectedItemId = R.id.menu_map
    }
}
