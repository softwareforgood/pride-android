package com.softwareforgood.pridefestival.data

import com.f2prateek.rx.preferences2.Preference
import org.threeten.bp.Instant

object InstantPreferenceConverter : Preference.Converter<Instant> {
    override fun deserialize(serialized: String): Instant = Instant.ofEpochMilli(serialized.toLong())
    override fun serialize(value: Instant): String = value.toEpochMilli().toString()
}
