package com.softwareforgood.pridefestival.ui.vendor

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.willReturn
import com.softwareforgood.pridefestival.data.VendorLoader
import com.softwareforgood.pridefestival.test.RxTestOverrides
import com.softwareforgood.pridefestival.test.vendors
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class DefaultVendorPresenterTest {

    private val testScheduler = TestScheduler()

    @Rule @JvmField val rxTestOverride = RxTestOverrides(computationScheduler = testScheduler)

    @Mock lateinit var vendorLoader: VendorLoader
    @Mock lateinit var vendorAdapter: VendorAdapter
    @Mock lateinit var listDecor: StickyHeaderDecoration
    @Mock lateinit var vendorView: VendorView
    @Mock lateinit var recyclerView: RecyclerView
    @Mock lateinit var tryAgainButton: View

    @InjectMocks lateinit var classUnderTest: DefaultVendorPresenter

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        given { vendorView.recyclerView } willReturn { recyclerView }
        given { vendorView.tryAgainButton } willReturn { tryAgainButton }
    }

    @Test fun `should attach adapter and list decoration`() {
        given { vendorLoader.vendors } willReturn { Single.never() }

        classUnderTest.attachView(vendorView)

        then(recyclerView).should().adapter = vendorAdapter
        then(recyclerView).should().addItemDecoration(listDecor)
    }

    @Test fun `should load and show vendors`() {
        given { vendorLoader.vendors } willReturn { Single.just(emptyList()) }

        classUnderTest.attachView(vendorView)

        then(vendorAdapter).should().loadVendors(emptyList())
        then(vendorView).should().showVendorList()
    }

    @Test fun `should handle and error when loading vendors`() {
        given { vendorLoader.vendors } willReturn { Single.error(Throwable("🔥")) }

        classUnderTest.attachView(vendorView)

        then(vendorView).should().showError()
    }

    @Test fun `should detach adapter and remove item decor when view is detached`() {
        given { vendorLoader.vendors } willReturn { Single.never() }

        classUnderTest.attachView(vendorView)
        classUnderTest.detachView()

        then(recyclerView).should().adapter = isNull()
        then(recyclerView).should().removeItemDecoration(listDecor)
    }

    @Test fun `should dispose of disposables when view is detached`() {
        given { vendorLoader.vendors } willReturn { Single.never() }

        classUnderTest.attachView(vendorView)

        classUnderTest.vendorDisposable.size() shouldBe 1

        classUnderTest.detachView()

        classUnderTest.vendorDisposable.size() shouldBe 0
    }

    @Test fun `should try to load vendors when try again is pressed`() {
        given { vendorLoader.vendors } willReturn { Single.never() }

        classUnderTest.attachView(vendorView)

        val clickCaptor = argumentCaptor<View.OnClickListener>()
        then(tryAgainButton).should().setOnClickListener(clickCaptor.capture())

        clickCaptor.firstValue.onClick(tryAgainButton)

        then(vendorView).should().showSpinner()
        then(vendorLoader).should(times(2)).vendors
    }

    @Test fun `should load events with search query when one is input`() {
        given { vendorLoader.vendors } willReturn { Single.never() } willReturn { Single.just(vendors.toList()) }

        classUnderTest.attachView(vendorView)
        classUnderTest.search(
            Observable.just("he", "hem").map { SearchViewQueryTextEvent.create(mock(), it, false) }
        )

        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS)

        then(vendorLoader).should(times(2)).vendors
        then(vendorAdapter).should().loadVendors(vendors.toList().dropLast(1))
        then(vendorView).should().showVendorList()
    }
}
