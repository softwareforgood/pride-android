package com.softwareforgood.pridefestival.data

import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.HasParseId
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.util.subscribeOnIoScheduler
import dagger.Reusable
import io.github.roguesdev.hoard.rxjava2.RxDepositor
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

interface FavoritesStorage {
    val events: Single<List<Event>>
    val parades: Single<List<ParadeEvent>>
    val vendors: Single<List<Vendor>>
    fun saveEvent(event: Event): Completable
    fun deleteEvent(event: Event): Completable
    fun saveParade(parade: ParadeEvent): Completable
    fun deleteParade(parade: ParadeEvent): Completable
    fun saveVendor(vendor: Vendor): Completable
    fun deleteVendor(vendor: Vendor): Completable
    fun hasEvent(event: Event): Single<Boolean>
    fun hasParade(parade: ParadeEvent): Single<Boolean>
    fun hasVendor(vendor: Vendor): Single<Boolean>
}

@Reusable
class DefaultFavoritesStorage @Inject constructor(
    @EventsDepositor private val eventsDepositor: RxDepositor<Set<String>>,
    @ParadeDepositor private val paradeDepositor: RxDepositor<Set<String>>,
    @VendorDepositor private val vendorDepositor: RxDepositor<Set<String>>,
    private val eventsLoader: EventsLoader,
    private val paradeLoader: ParadeLoader,
    private val vendorLoader: VendorLoader
) : FavoritesStorage {

    override fun hasEvent(event: Event) = eventsDepositor.hasA(event)
    override fun saveEvent(event: Event) = eventsDepositor.save(event)
    override fun deleteEvent(event: Event) = eventsDepositor.delete(event)
    override val events: Single<List<Event>>
        get() = eventsDepositor.betterRetrieve { observable ->
            observable.flatMapSingle(eventsLoader::getEvent)
                    .doOnError { Timber.e(it, "Error retrieving Event") }
        }.onErrorReturnItem(emptyList<Event>())

    override fun hasParade(parade: ParadeEvent) = paradeDepositor.hasA(parade)
    override fun saveParade(parade: ParadeEvent) = paradeDepositor.save(parade)
    override fun deleteParade(parade: ParadeEvent) = paradeDepositor.delete(parade)
    override val parades: Single<List<ParadeEvent>>
        get() = paradeDepositor.betterRetrieve { observable ->
            observable.flatMapSingle(paradeLoader::getParade)
                    .doOnError { Timber.e(it, "Error retrieving parade") }
        }.onErrorReturnItem(emptyList())

    override fun hasVendor(vendor: Vendor) = vendorDepositor.hasA(vendor)
    override fun saveVendor(vendor: Vendor) = vendorDepositor.save(vendor)
    override fun deleteVendor(vendor: Vendor) = vendorDepositor.delete(vendor)
    override val vendors: Single<List<Vendor>>
        get() = vendorDepositor.betterRetrieve { observable ->
            observable.flatMapSingle(vendorLoader::getVendor)
                .doOnError { Timber.e(it, "Error retrieving vendor") }
        }.onErrorReturnItem(emptyList())

    private fun <T : HasParseId> RxDepositor<Set<String>>.hasA(item: T) = retrieve()
            .onErrorReturn { emptySet() }
            .map { it.contains(item.objectId) }
            .subscribeOnIoScheduler()
            .doOnError { Timber.e(it, "Error checking for %s", item) }

    private fun <T : HasParseId> RxDepositor<Set<String>>.delete(item: T) = retrieve()
            .map { it - item.objectId }
            .flatMapCompletable { store(it) }
            .subscribeOnIoScheduler()
            .doOnError { Timber.e(it, "Error deleting %s", item) }!!

    private fun <T : HasParseId> RxDepositor<Set<String>>.save(item: T) = retrieve()
            .onErrorReturn { emptySet() }
            .map { it + item.objectId }
            .flatMapCompletable { store(it) }
            .subscribeOnIoScheduler()
            .doOnError { Timber.e(it, "Error saving %s", item) }!!

    private inline fun <T> RxDepositor<Set<String>>.betterRetrieve(
        input: (Observable<String>) -> Observable<T>
    ) = retrieve()
            .toObservable()
            .flatMapIterable { it }
            .run(input)
            .toList()
            .subscribeOnIoScheduler()
            .doOnError { Timber.e(it, "Error retrieving data") }
}
