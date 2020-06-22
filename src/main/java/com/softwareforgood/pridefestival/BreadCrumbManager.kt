package com.softwareforgood.pridefestival

import javax.inject.Inject

@ApplicationScope
class BreadCrumbManager @Inject constructor(
    private val breadCrumbLoggers: Set<@JvmSuppressWildcards BreadCrumbLogger>
) {
    fun logBreadCrumb(message: String) {
        breadCrumbLoggers.forEach { it.logBreadCrumb(message) }
    }
}
