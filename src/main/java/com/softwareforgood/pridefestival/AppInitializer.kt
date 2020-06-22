package com.softwareforgood.pridefestival

/**
 * Interface to be inherited by debug and release variants to provide
 * specific setup for each variant
 */
interface AppInitializer {
    operator fun invoke()
}
