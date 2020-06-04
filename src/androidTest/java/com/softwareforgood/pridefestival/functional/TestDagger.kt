package com.softwareforgood.pridefestival.functional

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.softwareforgood.pridefestival.FirebaseRemoteConfigModule
import com.softwareforgood.pridefestival.data.DataModule
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [DataModule::class, TestModule::class])
interface TestComponent {
    fun inject(test: EventsLoaderFunctionalTest)
    fun inject(test: InfoLoaderFunctionalTest)
    fun inject(test: ParadeLoaderFunctionalTest)
    fun inject(test: VendorLoaderFunctionalTest)
}

@Module(includes = [FirebaseRemoteConfigModule::class])
object TestModule {
    @JvmStatic
    @Provides
    fun provideRxSharedPrefs() = RxSharedPreferences.create(
            PreferenceManager.getDefaultSharedPreferences(testContext)
    )
}
