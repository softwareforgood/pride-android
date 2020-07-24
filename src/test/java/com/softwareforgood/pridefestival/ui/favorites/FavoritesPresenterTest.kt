package com.softwareforgood.pridefestival.ui.favorites

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.willReturn
import com.softwareforgood.pridefestival.test.RxTestOverrides
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class FavoritesPresenterTest {

    private val testScheduler = TestScheduler()

    @JvmField @Rule val rxTestOverrides = RxTestOverrides(computationScheduler = testScheduler)

    @Mock lateinit var favoritesAdapter: FavoritesAdapter
    @Mock lateinit var listDecor: StickyHeaderDecoration
    @Mock lateinit var favoritesView: FavoritesView
    @Mock lateinit var recyclerView: RecyclerView
    @Mock lateinit var tryAgainButton: View

    @InjectMocks lateinit var classUnderTest: FavoritesPresenter

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        given { favoritesView.recyclerView } willReturn { recyclerView }
        given { favoritesView.tryAgainButton } willReturn { tryAgainButton }
    }

    @Test fun `recycler view should be setup when view is attached`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.complete() }

        classUnderTest.attachView(favoritesView)

        then(recyclerView).should().adapter = favoritesAdapter
        then(recyclerView).should().addItemDecoration(listDecor)
    }

    @Test fun `should load data on adapter and then show favorites list`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.complete() }
        given { favoritesAdapter.itemCount } willReturn { 2 }

        classUnderTest.attachView(favoritesView)

        then(favoritesAdapter).should().loadData()
        then(favoritesView).should().showFavoritesList()
    }

    @Test fun `should load data on adapter and then show empty message`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.complete() }
        given { favoritesAdapter.itemCount } willReturn { 0 }

        classUnderTest.attachView(favoritesView)

        then(favoritesAdapter).should().loadData()
        then(favoritesView).should().showEmptyMessage()
    }

    @Test fun `should show error view when loading data on adapter and there is an error`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.error(Throwable("☠️")) }

        classUnderTest.attachView(favoritesView)

        then(favoritesView).should().showError()
    }

    @Test fun `should remove adapter and list decor when view is detached`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.complete() }

        classUnderTest.attachView(favoritesView)
        classUnderTest.detachView()

        then(recyclerView).should().adapter = isNull()
        then(recyclerView).should().removeItemDecoration(listDecor)
    }

    @Test fun `should dispose of disposable when view is detached`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.complete() }

        classUnderTest.attachView(favoritesView)
        classUnderTest.favoritesDisposable.size() shouldBe 1

        classUnderTest.detachView()
        classUnderTest.favoritesDisposable.size() shouldBe 0
    }

    @Test fun `should load data when retry button is pressed`() {
        given { favoritesAdapter.loadData() } willReturn { Completable.complete() }

        classUnderTest.attachView(favoritesView)

        val onClickListenerCapture = argumentCaptor<View.OnClickListener>()
        then(tryAgainButton).should().setOnClickListener(onClickListenerCapture.capture())

        onClickListenerCapture.firstValue.onClick(tryAgainButton)

        then(favoritesAdapter).should(times(2)).loadData()
        then(favoritesView).should().showSpinner()
    }

    @Test fun `should load events with search query when one is input`() {
        given { favoritesAdapter.loadData(any()) } willReturn { Completable.complete() }
        given { favoritesAdapter.itemCount } willReturn { 2 }

        classUnderTest.attachView(favoritesView)
        classUnderTest.search(
            Observable.just("a", "b").map { SearchViewQueryTextEvent.create(mock(), it, false) }
        )

        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS)

        then(favoritesAdapter).should(times(2)).loadData(any())
        then(favoritesView).should(times(2)).showFavoritesList()
    }
}
