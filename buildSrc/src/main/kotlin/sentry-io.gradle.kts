plugins {
    id("com.android.application") apply false
}

val sentryIoDsn = when {
    hasProperty("com.softwareforgood.pridefestival.sentryIoDsn") -> properties["com.softwareforgood.pridefestival.sentryIoDsn"]
    else -> System.getenv("SOFTWARE_FOR_GOOD_PRIDE_FESTIVAL_SENTRY_IO_DSN") ?: ""
}

android {
    defaultConfig {
        buildConfigField("String", "SENTRY_IO_DSN", """"$sentryIoDsn"""")
    }
}
