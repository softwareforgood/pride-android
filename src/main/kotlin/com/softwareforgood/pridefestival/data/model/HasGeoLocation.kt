package com.softwareforgood.pridefestival.data.model

import com.parse.ParseGeoPoint

interface HasGeoLocation {
    val location: ParseGeoPoint?
}
