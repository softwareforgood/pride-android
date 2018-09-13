package com.softwareforgood.pridefestival.functional

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before

interface LoaderFunctionalTest {
    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { TestScheduler() }
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }
}
