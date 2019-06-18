package com.softwareforgood.pridefestival

import io.sentry.SentryClient
import io.sentry.event.BreadcrumbBuilder
import javax.inject.Inject

@ApplicationScope
class SentryBreadCrumbLogger @Inject constructor(
    private val sentryClient: SentryClient
): BreadCrumbLogger {
    override fun logBreadCrumb(message: String) {
        sentryClient.context.recordBreadcrumb(
            BreadcrumbBuilder().setMessage(message).build()
        )
    }
}
