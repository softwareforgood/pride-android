package com.softwareforgood.pridefestival.util

import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

fun ZonedDateTime.toTimeString(): String = format(timeFormatter)
fun ZonedDateTime.toDateString(): String = format(dateFormatter)
fun LocalDate.toDateString(): String = format(dateFormatter)
