package com.softwareforgood.pridefestival.ui.events

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
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.willReturn
import com.softwareforgood.pridefestival.data.EventsLoader
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.test.RxTestOverrides
import com.softwareforgood.pridefestival.test.events
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.amshove.kluent.`should be greater or equal to`
import org.amshove.kluent.`should be`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

class DefaultEventsPresenterTest {

    private val testScheduler = TestScheduler()
    @Rule @JvmField var rxOverride = RxTestOverrides(computationScheduler = testScheduler)

    @Mock lateinit var eventsView: EventsView
    @Mock lateinit var eventsLoader: EventsLoader
    @Mock lateinit var eventsAdapter: EventsAdapter
    @Mock lateinit var listDecor: StickyHeaderDecoration
    @Mock lateinit var recyclerView: RecyclerView
    @Mock lateinit var tryAgainButton: View

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)
        given { eventsView.recyclerView } willReturn { recyclerView }
        given { eventsView.tryAgainButton } willReturn { tryAgainButton }
    }

    @Test fun `should set adapter and add decoration to recycler view`() {
        // given
        given { eventsLoader.events } willReturn { Single.just(emptyList()) }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)

        // when
        classUnderTest.attachView(eventsView)

        // then
        verify(recyclerView).adapter = eventsAdapter
        verify(recyclerView).addItemDecoration(listDecor)
    }

    @Test fun `should load events and sort by name than start time`() {
        // given
        val time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1000), ZoneId.of("America/Chicago"))
        val event1 = Event(
                objectId = "1",
                verified = true,
                name = "Test1",
                startTime = time.minusDays(1)
        )

        val event2 = Event(
                objectId = "2",
                verified = true,
                name = "B",
                startTime = time
        )

        val event3 = Event(
                objectId = "3",
                verified = true,
                name = "A",
                startTime = time
        )

        val events = listOf(event1, event2, event3)
        val expected = listOf(event1, event3, event2)

        given { eventsLoader.events } willReturn { Single.just(events) }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)

        // when
        classUnderTest.attachView(eventsView)

        // then
        verify(eventsAdapter).loadEvents(expected)
        verify(eventsView).showEventsList()
    }

    @Test fun `should correctly dispose of the load events call`() {
        // given
        given { eventsLoader.events } willReturn { Single.just(emptyList()) }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)

        // when
        classUnderTest.attachView(eventsView)

        // then
        classUnderTest.eventsDisposable.size() `should be greater or equal to` 1

        // and when
        classUnderTest.detachView()

        // then
        classUnderTest.eventsDisposable.size() `should be` 0
    }

    @Test fun `should show error view when there is an issue loading events`() {
        // given
        given { eventsLoader.events } willReturn { Single.error(Throwable("☠️")) }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)

        // when
        classUnderTest.attachView(eventsView)

        // then
        eventsView.showError()
    }

    @Test fun `should try to load events when try again is pressed`() {
        given { eventsLoader.events } willReturn { Single.never() }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)
        classUnderTest.attachView(eventsView)

        val captor = argumentCaptor<View.OnClickListener>()
        then(tryAgainButton).should().setOnClickListener(captor.capture())
        captor.firstValue.onClick(tryAgainButton)

        then(eventsView).should().showSpinner()
        then(eventsLoader).should(times(2)).events
    }

    @Test fun `should remove list decor from recycler view`() {
        given { eventsLoader.events } willReturn { Single.never() }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)
        classUnderTest.attachView(eventsView)
        classUnderTest.detachView()

        then(recyclerView).should().adapter = isNull()
    }

    @Test fun `should load events with search query when one is input`() {
        given { eventsLoader.events } willReturn { Single.never() } willReturn { Single.just(events.toList()) }

        val classUnderTest = DefaultEventsPresenter(eventsLoader, eventsAdapter, listDecor)
        classUnderTest.attachView(eventsView)
        classUnderTest.search(
            Observable.just("ch", "CHY")
                .map { SearchViewQueryTextEvent.create(mock(), it, false) }
        )

        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS)

        then(eventsLoader).should(times(2)).events
        then(eventsAdapter).should().loadEvents(events.toList())
        then(eventsView).should().showEventsList()
    }
}
