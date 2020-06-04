package com.softwareforgood.pridefestival.test

import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.data.model.VendorColor
import com.softwareforgood.pridefestival.data.model.VendorType
import org.threeten.bp.ZonedDateTime

val event = Event(
        objectId = "6e7fa854-b23f-49ef-871b-e157e047ac3c",
        name = "Itchy",
        verified = false,
        startTime = ZonedDateTime.now()
)

val event2 = Event(
        objectId = "45f338be-a83f-4e13-9f79-daa6475292d5",
        name = "Scratchy",
        verified = true,
        startTime = ZonedDateTime.now()
)

val events = setOf(event, event2)

val parade = ParadeEvent(
        objectId = "52f6c62d-dd74-4899-bd30-e14af6408276",
        name = "Ramona Schwertfeger",
        lineupNumber = 12
)

val parade2 = ParadeEvent(
        objectId = "eddf4625-4ce1-43bb-b9e2-1d499c9aad4c",
        name = "Anya Belt",
        lineupNumber = 65
)

val parades = setOf(parade, parade2)

val vendor = Vendor(
        objectId = "fb8afc35-ac31-4770-91cb-f3160268a68b",
        showInParadeList = false,
        verified = false,
        name = "Shemika Faison",
        isSponsor = false,
        sectionColor = VendorColor.ORANGE,
        details = null,
        location = null,
        locationName = null,
        logo = null,
        vendorType = VendorType.FOOD,
        website = null

)

val vendor2 = Vendor(
        objectId = "14742080-5297-476e-8fdd-2c949e4b06ad",
        showInParadeList = false,
        verified = false,
        name = "Willard Tabuena",
        isSponsor = false,
        sectionColor = VendorColor.BLUE,
        details = null,
        location = null,
        locationName = null,
        logo = null,
        vendorType = VendorType.NON_FOOD,
        website = null

)

val vendors = setOf(vendor, vendor2)
