package com.softwareforgood.pridefestival.functional

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwareforgood.pridefestival.data.EventsLoader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Functional test of the [EventsLoader] to ensure that it loads and filters data properly
 * from the parse server as well as detect any changes that may occur to the data that would
 * cause the app to crash.
 */
@RunWith(AndroidJUnit4::class)
@Suppress("FunctionName")
class EventsLoaderFunctionalTest : LoaderFunctionalTest {

    @Inject lateinit var classUnderTest: EventsLoader

    @Before override fun setup() {
        super.setup()
        DaggerTestComponent.create().inject(this)
    }

    @Test fun should_load_events_from_parse_into_models() {
        // when
        val test = classUnderTest.events.test()

        // then
        test.assertValue { it.isNotEmpty() }
                .assertComplete()
                .assertNoErrors()
    }
}
