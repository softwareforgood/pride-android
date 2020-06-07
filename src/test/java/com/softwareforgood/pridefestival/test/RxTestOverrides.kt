package com.softwareforgood.pridefestival.test

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RxTestOverrides(
    private val ioScheduler: Scheduler = Schedulers.trampoline(),
    private val androidScheduler: Scheduler = Schedulers.trampoline(),
    private val computationScheduler: Scheduler = TestScheduler()

) : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    RxJavaPlugins.setIoSchedulerHandler { ioScheduler }
                    RxJavaPlugins.setComputationSchedulerHandler { computationScheduler }
                    RxAndroidPlugins.setMainThreadSchedulerHandler { androidScheduler }
                    RxAndroidPlugins.setInitMainThreadSchedulerHandler { androidScheduler }

                    try {
                        base.evaluate()
                    } finally {
                        RxJavaPlugins.reset()
                        RxAndroidPlugins.reset()
                    }
                }
            }
}
