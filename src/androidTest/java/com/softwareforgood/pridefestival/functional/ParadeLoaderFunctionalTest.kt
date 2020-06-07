package com.softwareforgood.pridefestival.functional

import com.softwareforgood.pridefestival.data.ParadeLoader
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

@Suppress("FunctionName")
class ParadeLoaderFunctionalTest : LoaderFunctionalTest {

    @Inject lateinit var classUnderTest: ParadeLoader

    @Before override fun setup() {
        super.setup()
        DaggerTestComponent.create().inject(this)
    }
    @Test fun should_load_parade_events() {
        // when
        val test = classUnderTest.parades.test()

        // then
        test.assertValue { it.isNotEmpty() }
        test.assertComplete()
        test.assertNoErrors()
    }
}
