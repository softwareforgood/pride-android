package com.softwareforgood.pridefestival.ui.info

import android.view.View
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.willReturn
import com.softwareforgood.pridefestival.data.InfoLoader
import com.softwareforgood.pridefestival.test.RxTestOverrides
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.amshove.kluent.mock
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DefaultInfoPresenterTest {

    @Rule @JvmField var rxOverride = RxTestOverrides()

    @Mock lateinit var infoLoader: InfoLoader
    @Mock lateinit var infoView: InfoView
    @Mock lateinit var tryAgainButton: View

    @InjectMocks lateinit var classUnderTest: DefaultInfoPresenter

    @Before fun setup() {
        MockitoAnnotations.initMocks(this)

        given { infoView.tryAgainButton } willReturn { tryAgainButton }
    }

    @Test fun `should load info text`() {
        // given
        val expected = "Info Text"
        given { infoLoader.infoText } willReturn { Single.just(expected) }

        // when
        classUnderTest.attachView(infoView)

        // then
        then(infoView).should().showInfo(expected)
    }

    @Test fun `should properly dispose of load info text call`() {
        // given
        val mockDisposable = mock(Disposable::class)

        given { infoLoader.infoText } willReturn { Single.just("Test") }

        classUnderTest.eventsDisposable = mockDisposable

        // when
        classUnderTest.attachView(infoView)

        // then
        classUnderTest.eventsDisposable shouldNotBe mockDisposable

        // and when
        classUnderTest.eventsDisposable = mockDisposable
        classUnderTest.detachView()

        // then
        then(mockDisposable).should().dispose()
    }

    @Test fun `should handle error when loading info`() {
        given { infoLoader.infoText } willReturn { Single.error(Throwable("😜")) }

        classUnderTest.attachView(infoView)

        then(infoView).should().showError()
    }

    @Test fun `should try to load info when try again button is pressed`() {
        given { infoLoader.infoText } willReturn { Single.never() }

        classUnderTest.attachView(infoView)

        val clickCaptor = argumentCaptor<View.OnClickListener>()
        then(tryAgainButton).should().setOnClickListener(clickCaptor.capture())

        clickCaptor.firstValue.onClick(tryAgainButton)

        then(infoView).should().showSpinner()
        then(infoLoader).should(times(2)).infoText
    }
}
