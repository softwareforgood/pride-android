package com.softwareforgood.pridefestival.functional

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwareforgood.pridefestival.data.VendorLoader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
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
