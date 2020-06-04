package com.softwareforgood.pridefestival.functional

import com.softwareforgood.pridefestival.data.InfoLoader
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

@Suppress("FunctionName")
class InfoLoaderFunctionalTest : LoaderFunctionalTest {

    @Inject lateinit var classUnderTest: InfoLoader

    @Before
    override fun setup() {
        super.setup()
        DaggerTestComponent.create().inject(this)
    }

    @Test fun should_load_info() {
        // when
        val test = classUnderTest.infoText.test()

        // then
        test.assertValue { it.isNotEmpty() }
        test.assertComplete()
        test.assertNoErrors()
    }
}
