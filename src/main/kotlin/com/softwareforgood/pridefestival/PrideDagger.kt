package com.softwareforgood.pridefestival

import android.app.Application
import com.parse.Parse
import com.softwareforgood.pridefestival.data.DataModule
import com.softwareforgood.pridefestival.ui.ActivityComponent
import javax.inject.Scope
import com.f2prateek.rx.preferences2.RxSharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Qualifier
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Scope annotation class ApplicationScope

@ApplicationScope
@Component(modules = [PrideAppModule::class, DataModule::class])
interface PrideAppComponent {
    val parseConfig: Parse.Configuration
    val firstRunCachingService: FirstRunCachingService

    fun activityComponentBuilder(): ActivityComponent.Builder

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): PrideAppComponent
    }
}

@Module(includes = [FirebaseRemoteConfigModule::class])
object PrideAppModule {

    @JvmStatic
    @Provides
    @Reusable
    fun provideParseConfig(
        application: Application,
        firebaseRemoteConfig: FirebaseRemoteConfig,
        @ParseServerUrl parseServerUrl: String
    ): Parse.Configuration = Parse.Configuration.Builder(application)
                .applicationId(firebaseRemoteConfig.getString("parse_application_id"))
                .server(parseServerUrl)
                .enableLocalDataStore()
                .build()

    @JvmStatic
    @Provides
    @Reusable
    fun provideRxSharedPreferences(application: Application): RxSharedPreferences {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application)
        return RxSharedPreferences.create(sharedPrefs)
    }

    @JvmStatic
    @Provides
    @Reusable
    @ParseServerUrl
    fun provideParseServerUrl(
        firebaseRemoteConfig: FirebaseRemoteConfig,
        @UseStagingUrlPref useStagingUrl: Preference<Boolean>
    ): String = if (useStagingUrl.get()) firebaseRemoteConfig.getString("parse_stage_url")
    else firebaseRemoteConfig.getString("parse_url")

    /**
     * Allows overrides on debug builds with Stetho.
     */
    @JvmStatic
    @Provides
    @Reusable
    @UseStagingUrlPref
    fun provideUseStagePref(rxPrefs: RxSharedPreferences) = rxPrefs.getBoolean("use-staging-url", false)

    @JvmStatic
    @Provides
    @Reusable
    @FirstRunPref
    fun provideFirstRunPref(rxPrefs: RxSharedPreferences) = rxPrefs.getBoolean("first-run", true)
}

@Qualifier @Target(FIELD, FUNCTION, VALUE_PARAMETER) annotation class UseStagingUrlPref
@Qualifier annotation class FirstRunPref
@Qualifier annotation class ParseServerUrl
