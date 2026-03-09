package com.team2.meetspace.data.entities

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Meeting(
    val id: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val users: List<UserContact> = emptyList(),
    val isImmediate: Boolean = true
) {
    val formattedDateTime: String
        get() = if (isImmediate) "Прямо сейчас" else {
            "${date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
}