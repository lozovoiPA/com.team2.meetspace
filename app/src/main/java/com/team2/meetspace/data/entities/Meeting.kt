package com.team2.meetspace.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

import java.time.format.DateTimeFormatter

@Entity(tableName = "meetings")
data class MeetingDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp: Int,
    @ColumnInfo(name = "room_identifier") val roomIdentifier: String,
    val description: String
) { }


data class Meeting(
    val timestamp: Int,
    val roomIdentifier: String,
    val description: String,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val users: List<UserContact> = emptyList(),
    val isImmediate: Boolean = true
) {
    public fun toDbEntity(): MeetingDbEntity = MeetingDbEntity(
        id = 0,
        timestamp = timestamp,
        roomIdentifier = roomIdentifier,
        description = description
    )
    val formattedDateTime: String
        get() = if (isImmediate) "Прямо сейчас" else {
            "${date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
}

