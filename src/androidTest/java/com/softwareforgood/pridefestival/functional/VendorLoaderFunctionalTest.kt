package com.softwareforgood.pridefestival.functional

import com.softwareforgood.pridefestival.data.VendorLoader
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

@Suppress("FunctionName")
class VendorLoaderFunctionalTest : LoaderFunctionalTest {

    @Inject lateinit var classUnderTest: VendorLoader

    @Before override fun setup() {
        super.setup()
        DaggerTestComponent.create().inject(this)
    }

    @Test fun should_load_vendors() {
        // when
        val test = classUnderTest.vendors.test()

        // then
        test.assertValue { it.isNotEmpty() }
        test.assertComplete()
        test.assertNoErrors()
    }
}
