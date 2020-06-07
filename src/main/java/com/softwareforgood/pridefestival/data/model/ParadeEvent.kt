package com.softwareforgood.pridefestival.data.model

data class ParadeEvent(
    override val objectId: String,
    val name: String,
    val details: String? = null,
    val lineupNumber: Int,
    val verified: Boolean = false
) : HasParseId
