package com.softwareforgood.pridefestival

import android.app.Application
import com.facebook.stetho.Stetho
import com.softwareforgood.pridefestival.data.DataModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import timber.log.Timber
import dagger.multibindings.Multibinds

@ApplicationScope
@Component(modules = [DebugPrideModule::class, PrideAppModule::class, DataModule::class])
interface PrideAppComponent: BasePrideAppComponent {

    @Component.Builder
    interface Builder: BasePrideAppComponent.Builder {
        @BindsInstance
        override fun application(application: Application): Builder
        override fun build(): PrideAppComponent
    }
}

@Module
abstract class DebugPrideModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideAppInitializer(app: Application): AppInitializer = object : AppInitializer {
            override fun invoke() {
                Timber.plant(Timber.DebugTree())
                Stetho.initializeWithDefaults(app)
            }
        }
    }

    @Multibinds
    abstract fun breadCrumbLoggerSet(): Set<@JvmSuppressWildcards BreadCrumbLogger>
}
