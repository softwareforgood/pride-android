package com.softwareforgood.pridefestival.data.model

data class ParadeEvent(
    override val objectId: String,
    val name: String,
    val details: String? = null,
    val lineupNumber: Int,
    val verified: Boolean = false
) : HasParseId {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParadeEvent

        if (objectId != other.objectId) return false

        return true
    }

    override fun hashCode(): Int {
        return objectId.hashCode()
    }
}
