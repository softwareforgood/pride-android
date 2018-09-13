package com.softwareforgood.pridefestival.data

import com.f2prateek.rx.preferences2.Preference
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.willReturn
import com.parse.ParseObject
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.data.model.VendorColor
import com.softwareforgood.pridefestival.data.model.VendorType
import com.softwareforgood.pridefestival.test.RxTestOverrides
import com.softwareforgood.pridefestival.test.vendor
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.amshove.kluent.`it returns`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class DefaultVendorLoaderTest {

    @JvmField @Rule val rxTestOverrides = RxTestOverrides()

    @Mock lateinit var lastUpdated: Preference<Instant>
    @Mock lateinit var parse: ParseDelegate
    @Mock lateinit var clock: Clock

    private val classUnderTest by lazy {
        DefaultVendorLoader(
                lastUpdated = lastUpdated,
                updateDuration = Duration.ofHours(1),
                parse = parse,
                clock = clock
        )
    }

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test fun `should load vendors from network if last updated time plus duration is before now`() {
        val instant = Instant.ofEpochSecond(100)

        given { lastUpdated.asObservable() } willReturn { Observable.just(instant.minus(Duration.ofDays(1))).repeat() }
        given { parse.vendorsFromNetwork } willReturn { Single.just(emptyList()) }
        given { parse.vendorsFromDisk } willReturn { Single.error(Throwable("Should not be called")) }
        given { parse.pinAll(any()) } willReturn { Completable.complete() }
        given { clock.instant() } willReturn { instant }

        val result = classUnderTest.vendors.test()
        result.assertNoErrors().assertComplete().assertValue(emptyList())

        then(lastUpdated).should().asObservable()
        then(lastUpdated).should().set(instant)
        then(lastUpdated).shouldHaveNoMoreInteractions()
        then(parse).should().pinAll(emptyList())
        then(parse).should(times(2)).vendorsFromNetwork
        then(parse).should().vendorsFromDisk
        then(parse).shouldHaveNoMoreInteractions()
    }

    @Test fun `should load vendors from disk if last updated time plus duration is after now`() {
        val instant = Instant.ofEpochMilli(3000)

        val parseObject = mock<ParseObject> {
            on { objectId } `it returns` "123456"
            on { getString("website") } `it returns` "http://example.com"
        }

        given { lastUpdated.asObservable() } willReturn { Observable.just(instant).repeat() }
        given { parse.vendorsFromNetwork } willReturn { Single.error(Throwable("Should not be called")) }
        given { parse.vendorsFromDisk } willReturn { Single.just(listOf(parseObject)) }
        given { parse.pinAll(any()) } willReturn { Completable.complete() }
        given { clock.instant() } willReturn { instant.plusMillis(500) }

        classUnderTest.vendors.test().assertNoErrors().assertComplete().assertValue(
                listOf(
                        Vendor(
                                objectId = "123456",
                                verified = false,
                                name = "",
                                website = "http://example.com",
                                details = null,
                                isSponsor = false,
                                location = null,
                                locationName = null,
                                logo = null,
                                sectionColor = VendorColor.UNKNOWN,
                                showInParadeList = false,
                                vendorType = VendorType.UNKNOWN
                        )
                )
        )

        then(lastUpdated).should().asObservable()
        then(lastUpdated).shouldHaveNoMoreInteractions()
        then(parse).should(times(2)).vendorsFromNetwork
        then(parse).should(times(2)).vendorsFromDisk
        then(parse).shouldHaveNoMoreInteractions()
    }

    @Test fun `should get vendor from disk`() {
        given { parse.getVendorFromDisk(any()) } willReturn { Single.just(vendor) }
        given { parse.getVendorFromNetwork(any()) } willReturn { Single.error(Throwable("Should not be called")) }
        val result = classUnderTest.getVendor("12345").test()

        result.assertNoErrors().assertComplete().assertValue(vendor)

        then(lastUpdated).shouldHaveZeroInteractions()
        then(parse).should().getVendorFromDisk("12345")
        then(parse).should().getVendorFromNetwork("12345")
        then(parse).shouldHaveNoMoreInteractions()
    }

    @Test fun `should get vendor from network if not available from disk`() {
        given { parse.getVendorFromDisk(any()) } willReturn { Single.error(Throwable("Object not found")) }
        given { parse.getVendorFromNetwork(any()) } willReturn { Single.just(vendor) }
        val result = classUnderTest.getVendor("12345").test()

        result.assertNoErrors().assertComplete().assertValue(vendor)

        then(lastUpdated).shouldHaveZeroInteractions()
        then(parse).should().getVendorFromDisk("12345")
        then(parse).should().getVendorFromNetwork("12345")
        then(parse).shouldHaveNoMoreInteractions()
    }
}
