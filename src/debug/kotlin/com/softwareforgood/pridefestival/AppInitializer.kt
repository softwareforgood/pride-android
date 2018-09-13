package com.softwareforgood.pridefestival

import android.app.Application
import com.facebook.stetho.Stetho
import timber.log.Timber

fun initialize(app: Application) {
    Timber.plant(Timber.DebugTree())
    Stetho.initializeWithDefaults(app)
}
