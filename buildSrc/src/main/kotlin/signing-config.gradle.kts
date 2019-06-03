extra["keystorePassword"] = when {
    hasProperty("com.softwareforgood.pridefestival.keystorePassword") -> properties["com.softwareforgood.pridefestival.keystorePassword"]
    else -> System.getenv("SOFTWARE_FOR_GOOD_PRIDE_FESTIVAL_KEYSTORE_PASSWORD") ?: "android"
}

extra["aliasKeyPassword"] = when {
    hasProperty("com.softwareforgood.pridefestival.aliasKeyPassword") -> properties["com.softwareforgood.pridefestival.aliasKeyPassword"]
    else -> System.getenv("SOFTWARE_FOR_GOOD_PRIDE_FESTIVAL_KEY_PASSWORD") ?: "android"
}

extra["storeKeyAlias"] = when {
    hasProperty("com.softwareforgood.pridefestival.storeKeyAlias") -> properties["com.softwareforgood.pridefestival.storeKeyAlias"]
    else -> System.getenv("SOFTWARE_FOR_GOOD_PRIDE_FESTIVAL_KEY_ALIAS") ?: "androiddebugkey"
}

extra["keystoreLocation"] = when {
    hasProperty("com.softwareforgood.pridefestival.keystoreLocation") -> properties["com.softwareforgood.pridefestival.keystoreLocation"]
    else -> System.getenv("SOFTWARE_FOR_GOOD_PRIDE_FESTIVAL_KEYSTORE_LOCATION") ?: "$rootDir/keystore/debug.keystore"
}
