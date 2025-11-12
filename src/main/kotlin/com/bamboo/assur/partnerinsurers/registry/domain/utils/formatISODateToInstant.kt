package com.bamboo.assur.partnerinsurers.registry.domain.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.ExperimentalTime
import kotlin.time.Instant



/**
 * Converts an ISO-formatted date string to an Instant object.
 *
 * The date string is first parsed into a LocalDate object using the ISO_DATE format.
 * The resulting LocalDate object is then converted to an OffsetDateTime object by
 * setting the time to 00:00 and the offset to the time zone of "AFRICA/LIBREVILLE".
 * The resulting OffsetDateTime object is then converted to an Instant object.
 *
 * @return The Instant object, which is equivalent to the given date string.
 *
 * @throws DateTimeParseException If the given string cannot be parsed to a date.
 */
@OptIn(ExperimentalTime::class)
fun String.formatISODateToInstant(): Instant {
    val timeZone = TimeZone.of("Africa/Libreville")
    val localDate = LocalDate.parse(this, LocalDate.Formats.ISO)
    val offsetDateTime = localDate.atStartOfDayIn(timeZone)

    return offsetDateTime
}
