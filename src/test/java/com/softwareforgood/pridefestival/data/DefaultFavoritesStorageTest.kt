package com.softwareforgood.pridefestival.data

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.willReturn
import com.softwareforgood.pridefestival.test.RxTestOverrides
import com.softwareforgood.pridefestival.test.event
import com.softwareforgood.pridefestival.test.event2
import com.softwareforgood.pridefestival.test.events
import com.softwareforgood.pridefestival.test.parade
import com.softwareforgood.pridefestival.test.parade2
import com.softwareforgood.pridefestival.test.parades
import com.softwareforgood.pridefestival.test.vendor
import com.softwareforgood.pridefestival.test.vendor2
import com.softwareforgood.pridefestival.test.vendors
import io.github.roguesdev.hoard.rxjava2.RxDepositor
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DefaultFavoritesStorageTest {

    @Rule
    @JvmField
    val rxOverrides = RxTestOverrides()

    @Mock lateinit var eventsDepositor: RxDepositor<Set<String>>
    @Mock lateinit var paradeDepositor: RxDepositor<Set<String>>
    @Mock lateinit var vendorDepositor: RxDepositor<Set<String>>
    @Mock lateinit var eventsLoader: EventsLoader
    @Mock lateinit var paradeLoader: ParadeLoader
    @Mock lateinit var vendorLoader: VendorLoader

    private lateinit var classUnderTest: DefaultFavoritesStorage

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        classUnderTest = DefaultFavoritesStorage(
                eventsDepositor = eventsDepositor,
                paradeDepositor = paradeDepositor,
                vendorDepositor = vendorDepositor,
                eventsLoader = eventsLoader,
                paradeLoader = paradeLoader,
                vendorLoader = vendorLoader
        )
    }

    @Test fun `hasEvent should return false when there is an error`() {
        given { eventsDepositor.retrieve() } willReturn { Single.error(Throwable("No Value")) }

        val result = classUnderTest.hasEvent(event).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasEvent should return false when there is nothing stored`() {
        given { eventsDepositor.retrieve() } willReturn { Single.just(emptySet()) }

        val result = classUnderTest.hasEvent(event).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasEvent should return false when there is not an objectId match`() {
        given { eventsDepositor.retrieve() } willReturn { Single.just(setOf("acdefg", "zxwy")) }

        val result = classUnderTest.hasEvent(event).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasEvent should return true when the event is stored`() {
        given { eventsDepositor.retrieve() } willReturn { Single.just(setOf("acdefg", event.objectId, "zxwy")) }

        val result = classUnderTest.hasEvent(event).test()

        result.assertValue(true).assertComplete().assertNoErrors()
    }

    @Test fun `should save an event when no previous values available`() {
        given { eventsDepositor.retrieve() } willReturn { Single.error(Throwable("No Values")) }
        given { eventsDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.saveEvent(event).test()

        then(eventsDepositor).should().store(setOf(event.objectId))
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should save an event with the other events`() {
        val originalObjectIds = setOf("12345", "54312")
        given { eventsDepositor.retrieve() } willReturn { Single.just(originalObjectIds) }
        given { eventsDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.saveEvent(event).test()

        then(eventsDepositor).should().store(originalObjectIds + event.objectId)
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should delete an event`() {
        val objectIds = setOf("12345", event.objectId)
        given { eventsDepositor.retrieve() } willReturn { Single.just(objectIds) }
        given { eventsDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.deleteEvent(event).test()

        then(eventsDepositor).should().store(objectIds - event.objectId)
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should return empty list when no events available`() {
        given { eventsDepositor.retrieve() } willReturn { Single.error(Throwable("No Values")) }

        val result = classUnderTest.events.test()

        result.assertNoErrors().assertValue(emptyList()).assertComplete()
    }

    @Test fun `should return events`() {
        val objectIds = events.map { it.objectId }.toSet()
        given { eventsDepositor.retrieve() } willReturn { Single.just(objectIds) }
        given { eventsLoader.getEvent(event.objectId) } willReturn { Single.just(event) }
        given { eventsLoader.getEvent(event2.objectId) } willReturn { Single.just(event2) }

        val result = classUnderTest.events.test()

        result.assertNoErrors().assertValue(events.toList()).assertComplete()
    }

    @Test
    fun `hasParade should return false when there is an error`() {
        given { paradeDepositor.retrieve() } willReturn { Single.error(Throwable("Oh No!")) }

        val result = classUnderTest.hasParade(parade).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test
    fun `hasParade should return false when there is nothing stored`() {
        given { paradeDepositor.retrieve() } willReturn { Single.just(setOf()) }

        val result = classUnderTest.hasParade(parade).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasParade should return false when there is not an objectId match`() {
        given { paradeDepositor.retrieve() } willReturn { Single.just(setOf("acdefg", "zxwy")) }

        val result = classUnderTest.hasParade(parade).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasParade should return true when the event is stored`() {
        given { paradeDepositor.retrieve() } willReturn { Single.just(setOf("acdefg", parade.objectId, "zxwy")) }

        val result = classUnderTest.hasParade(parade).test()

        result.assertValue(true).assertComplete().assertNoErrors()
    }

    @Test fun `should save a parade when no previous values available`() {
        given { paradeDepositor.retrieve() } willReturn { Single.error(Throwable("No Values")) }
        given { paradeDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.saveParade(parade).test()

        then(paradeDepositor).should().store(setOf(parade.objectId))
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should save a parade with the other parade`() {
        val originalObjectIds = setOf("12345", "54312")
        given { paradeDepositor.retrieve() } willReturn { Single.just(originalObjectIds) }
        given { paradeDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.saveParade(parade).test()

        then(paradeDepositor).should().store(originalObjectIds + parade.objectId)
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should delete a parade`() {
        val objectIds = setOf("12345", parade.objectId)
        given { paradeDepositor.retrieve() } willReturn { Single.just(objectIds) }
        given { paradeDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.deleteParade(parade).test()

        then(paradeDepositor).should().store(objectIds - parade.objectId)
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should return empty list when no parades available`() {
        given { paradeDepositor.retrieve() } willReturn { Single.error(Throwable("No Values")) }

        val result = classUnderTest.parades.test()

        result.assertNoErrors().assertValue(emptyList()).assertComplete()
    }

    @Test fun `should return parades`() {
        val objectIds = events.map { it.objectId }.toSet()
        given { paradeDepositor.retrieve() } willReturn { Single.just(objectIds) }
        given { paradeLoader.getParade(event.objectId) } willReturn { Single.just(parade) }
        given { paradeLoader.getParade(event2.objectId) } willReturn { Single.just(parade2) }

        val result = classUnderTest.parades.test()

        result.assertNoErrors().assertValue(parades.toList()).assertComplete()
    }

    @Test fun `hasVendor should return false when there is an error`() {
        given { vendorDepositor.retrieve() } willReturn { Single.error(Throwable("No Value")) }

        val result = classUnderTest.hasVendor(vendor).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasVendor should return false when nothing is stored`() {
        given { vendorDepositor.retrieve() } willReturn { Single.just(emptySet()) }

        val result = classUnderTest.hasVendor(vendor).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasVendor should return false when there is not an objectId match`() {
        given { vendorDepositor.retrieve() } willReturn { Single.just(setOf("acdefg", "zxwy")) }

        val result = classUnderTest.hasVendor(vendor).test()

        result.assertValue(false).assertComplete().assertNoErrors()
    }

    @Test fun `hasVendor should return true when the event is stored`() {
        given { vendorDepositor.retrieve() } willReturn { Single.just(setOf("acdefg", vendor.objectId, "zxwy")) }

        val result = classUnderTest.hasVendor(vendor).test()

        result.assertValue(true).assertComplete().assertNoErrors()
    }

    @Test fun `should save an vendor when no previous values available`() {
        given { vendorDepositor.retrieve() } willReturn { Single.error(Throwable("No Values")) }
        given { vendorDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.saveVendor(vendor).test()

        then(vendorDepositor).should().store(setOf(vendor.objectId))
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should save an vendor with the other vendors`() {
        val originalObjectIds = setOf("12345", "54312")
        given { vendorDepositor.retrieve() } willReturn { Single.just(originalObjectIds) }
        given { vendorDepositor.store(any()) } willReturn { Completable.complete() }

        val result = classUnderTest.saveVendor(vendor).test()

        then(vendorDepositor).should().store(originalObjectIds + vendor.objectId)
        result.assertComplete().assertNoErrors()
    }

    @Test fun `should return empty list when no vendors available`() {
        given { vendorDepositor.retrieve() } willReturn { Single.error(Throwable("No Values")) }

        val result = classUnderTest.vendors.test()

        result.assertNoErrors().assertValue(emptyList()).assertComplete()
    }

    @Test fun `should return vendors`() {
        val objectIds = vendors.map { it.objectId }.toSet()
        given { vendorDepositor.retrieve() } willReturn { Single.just(objectIds) }
        given { vendorLoader.getVendor(vendor.objectId) } willReturn { Single.just(vendor) }
        given { vendorLoader.getVendor(vendor2.objectId) } willReturn { Single.just(vendor2) }

        val result = classUnderTest.vendors.test()

        result.assertNoErrors().assertValue(vendors.toList()).assertComplete()
    }
}
