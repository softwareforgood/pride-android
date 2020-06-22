package com.softwareforgood.pridefestival

import android.app.Application
import com.softwareforgood.pridefestival.data.DataModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.sentry.Sentry
import io.sentry.SentryClient
import io.sentry.SentryClientFactory
import io.sentry.android.AndroidSentryClientFactory
import timber.log.Timber

@ApplicationScope
@Component(modules = [ReleasePrideModule::class, PrideAppModule::class, DataModule::class])
interface PrideAppComponent: BasePrideAppComponent {

    @Component.Builder
    interface Builder: BasePrideAppComponent.Builder {
        @BindsInstance
        override fun application(application: Application): Builder
        override fun build(): PrideAppComponent
    }
}

@Module
object ReleasePrideModule {
    @JvmStatic
    @Provides
    fun provideAppInitializer(app: Application, sentryTree: SentryTree): AppInitializer = object : AppInitializer {
        override fun invoke() {
            Sentry.init(BuildConfig.SENTRY_IO_DSN, AndroidSentryClientFactory(app))
            Timber.plant(sentryTree)
        }
    }

    @JvmStatic
    @Provides
    @ApplicationScope
    fun provideSentryClient(): SentryClient = SentryClientFactory.sentryClient()

    @JvmStatic
    @Provides
    @IntoSet
    fun provideSentryBreadCrumbLoggerIntoSet(logger: SentryBreadCrumbLogger): BreadCrumbLogger = logger
}

