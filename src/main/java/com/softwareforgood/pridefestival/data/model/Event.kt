package com.softwareforgood.pridefestival.data.model

import com.parse.ParseFile
import com.parse.ParseGeoPoint
import org.threeten.bp.ZonedDateTime
import java.io.Serializable

data class Event(
    override val objectId: String,
    val locationName: String? = null,
    val details: String? = null,
    val verified: Boolean,
    val name: String,
    override val location: ParseGeoPoint? = null,
    val startTime: ZonedDateTime? = null,
    val type: EventType = EventType.MISCELLANEOUS,
    val image: ParseFile? = null,
    val website: String? = null
) : Mappable, HasParseId, HasGeoLocation, Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (objectId != other.objectId) return false

        return true
    }

    override fun hashCode(): Int {
        return objectId.hashCode()
    }
}

enum class EventType(val parseText: String) {
    PERFORMANCE("performance"),
    MUSIC("music"),
    SPORTS("sports"),
    FOOD("food"),
    MISCELLANEOUS("");

    companion object {
        fun fromParseText(parseText: String?) =
                values().find { it.parseText == parseText } ?: MISCELLANEOUS
    }
}
