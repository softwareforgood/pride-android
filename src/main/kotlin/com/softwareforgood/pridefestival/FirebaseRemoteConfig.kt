package com.softwareforgood.pridefestival

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.softwareforgood.pridefestival.FirebaseRemoteConfigConstants.PARSE_APPLICATION_ID
import com.softwareforgood.pridefestival.FirebaseRemoteConfigConstants.PARSE_SERVER_REFRESH_RATE_IN_MINS
import com.softwareforgood.pridefestival.FirebaseRemoteConfigConstants.PARSE_STAGE_URL
import com.softwareforgood.pridefestival.FirebaseRemoteConfigConstants.PARSE_URL
import dagger.Module
import dagger.Provides
import dagger.Reusable
import timber.log.Timber

object FirebaseRemoteConfigConstants {
    const val PARSE_STAGE_URL = "parse_stage_url"
    const val PARSE_URL = "parse_url"
    const val PARSE_APPLICATION_ID = "parse_application_id"
    const val PARSE_SERVER_REFRESH_RATE_IN_MINS = "parse_server_refresh_rate_mins"
}

@Module object FirebaseRemoteConfigModule {
    @JvmStatic
    @Provides
    @Reusable
    fun provideRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            .also { config ->
                val firebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build()
                config.setConfigSettings(firebaseRemoteConfigSettings)

                // load defaults just in case
                // or while fetch takes place to load the initial config
                config.setDefaults(mapOf<String, Any>(
                        PARSE_STAGE_URL to "https://pride-festival-parse-staging.herokuapp.com/parse",
                        PARSE_URL to "https://pride-festival-parse.herokuapp.com/parse",
                        PARSE_APPLICATION_ID to "kl37CbvvFZGY1diRa7hbMG6X3ZQGcsKZEQRpNMHA",
                        PARSE_SERVER_REFRESH_RATE_IN_MINS to 60L
                ))

                config.fetch()
                        .addOnSuccessListener {
                            Timber.d("loaded remote config")
                            config.activateFetched()
                        }.addOnFailureListener {
                            Timber.e(it, "Error loading remote config")
                        }
            }
}
