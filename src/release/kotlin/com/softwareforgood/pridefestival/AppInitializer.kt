package com.softwareforgood.pridefestival

import timber.log.Timber

fun initialize(app: PrideApp) {
    Timber.plant(Timber.DebugTree())
    Timber.plant(CrashlyticsTree)
}
