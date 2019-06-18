package com.softwareforgood.pridefestival

import com.f2prateek.rx.preferences2.Preference
import com.softwareforgood.pridefestival.data.EventsLoader
import com.softwareforgood.pridefestival.data.InfoLoader
import com.softwareforgood.pridefestival.data.ParadeLoader
import com.softwareforgood.pridefestival.data.VendorLoader
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import javax.inject.Inject

/**
 * Calls each loader which will cache data to the local database and be used. This will
 * ensure all data is there if offline mode is used.
 */
class FirstRunCachingService @Inject constructor(
    private val eventsLoader: EventsLoader,
    private val infoLoader: InfoLoader,
    private val paradeLoader: ParadeLoader,
    private val vendorLoader: VendorLoader,
    @FirstRunPref private val firstRunPref: Preference<Boolean>
) {
    fun cacheParseObjects(): Disposable {
        firstRunPref.get() == true || return Disposables.empty()

        val events = eventsLoader.events
                .doOnError { Timber.e(it, "Error loading events") }
                .retry(3)

        val info = infoLoader.infoText
                .doOnError { Timber.e(it, "Error loading info") }
                .retry(3)

        val parade = paradeLoader.parades
                .doOnError { Timber.e(it, "Error loading parades") }
                .retry(3)

        val vendor = vendorLoader.vendors
                .doOnError { Timber.e(it, "Error loading vendors") }
                .retry(3)

        return Single.mergeDelayError(events, info, parade, vendor)
                .subscribe({
                    firstRunPref.set(false)
                }, { Timber.e(it, "Error loading data on first run") })
    }
}
