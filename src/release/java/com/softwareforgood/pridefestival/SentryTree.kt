package com.softwareforgood.pridefestival

import android.util.Log
import io.sentry.SentryClient
import timber.log.Timber.DebugTree
import javax.inject.Inject

@ApplicationScope
class SentryTree @Inject constructor(
    private val sentry: SentryClient
): DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.INFO) {
            return
        }

        sentry.sendMessage("$tag: $message")
        if (t != null) {
            sentry.sendException(t)
        }
    }
}
