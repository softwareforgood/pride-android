package com.softwareforgood.pridefestival

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

object CrashlyticsTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority > Log.DEBUG)
        Crashlytics.log(priority, tag, message)

        if (t == null) return
        Crashlytics.logException(t)
    }
}
