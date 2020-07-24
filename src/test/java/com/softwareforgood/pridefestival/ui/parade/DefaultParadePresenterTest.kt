package com.softwareforgood.pridefestival.ui.parade

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.willReturn
import com.softwareforgood.pridefestival.data.ParadeLoader
import com.softwareforgood.pridefestival.test.RxTestOverrides
import com.softwareforgood.pridefestival.test.parade
import com.softwareforgood.pridefestival.test.parade2
import com.softwareforgood.pridefestival.test.parades
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

class DefaultParadePresenterTest {

    private val testScheduler = TestScheduler()

    @Rule @JvmField var rxOverride = RxTestOverrides(computationScheduler = testScheduler)

    @Mock lateinit var paradeLoader: ParadeLoader
    @Mock lateinit var paradeView: ParadeView
    @Mock lateinit var paradeAdapter: ParadeAdapter
    @Mock lateinit var recyclerView: RecyclerView
    @Mock lateinit var tryAgainButton: View

    @InjectMocks lateinit var classUnderTest: DefaultParadePresenter

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        given { paradeView.recyclerView } willReturn { recyclerView }
        given { paradeView.tryAgainButton } willReturn { tryAgainButton }
    }

    @Test fun `should set adapter on recycler view`() {
        // given
        given { paradeLoader.parades } willReturn { Single.just(emptyList()) }

        // when
        classUnderTest.attachView(paradeView)

        // then
        verify(recyclerView).adapter = paradeAdapter
    }

    @Test fun `should load and sort parade events`() {
        // given
        val paradeEvents = listOf(parade, parade2)
        val expected = paradeEvents.sortedBy { it.lineupNumber }

        given { paradeLoader.parades } willReturn { Single.just(paradeEvents) }

        // when
        classUnderTest.attachView(paradeView)

        // then
        verify(paradeAdapter).loadParadeEvents(expected)
        verify(paradeView).showParadeList()
    }

    @Test fun `should properly dispose of load parade events call`() {
        // given
        given { paradeLoader.parades } willReturn { Single.just(emptyList()) }

        val classUnderTest = DefaultParadePresenter(paradeLoader, paradeAdapter)

        // when
        classUnderTest.attachView(paradeView)

        // then
        classUnderTest.eventsDisposable.size() shouldBe 1

        // and when
        classUnderTest.detachView()

        // then
        classUnderTest.eventsDisposable.size() shouldBe 0
    }

    @Test fun `should handle error when loading parade events`() {
        given { paradeLoader.parades } willReturn { Single.error(Throwable("☠️")) }

        classUnderTest.attachView(paradeView)

        then(paradeView).should().showError()
    }

    @Test fun `should try again when try again button is pressed`() {
        given { paradeLoader.parades } willReturn { Single.never() }

        classUnderTest.attachView(paradeView)

        val clickCaptor = argumentCaptor<View.OnClickListener>()
        then(tryAgainButton).should().setOnClickListener(clickCaptor.capture())

        clickCaptor.firstValue.onClick(tryAgainButton)

        then(paradeView).should().showSpinner()
        then(paradeLoader).should(times(2)).parades
    }

    @Test fun `should load events with search query when one is input`() {
        given { paradeLoader.parades } willReturn { Single.never() } willReturn { Single.just(parades.toList()) }

        classUnderTest.attachView(paradeView)
        classUnderTest.search(
            Observable.just("c", "CH").map { SearchViewQueryTextEvent.create(mock(), it, false) }
        )

        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS)

        then(paradeLoader).should(times(2)).parades
        then(paradeAdapter).should().loadParadeEvents(parades.toList().dropLast(1))
        then(paradeView).should().showParadeList()
    }
}
