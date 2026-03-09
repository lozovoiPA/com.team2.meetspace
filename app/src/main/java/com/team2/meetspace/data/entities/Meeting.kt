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
    val users: List<UserContact> = emptyList()
) {
    companion object {
        public val emptyMeeting: Meeting = Meeting(0, "", "");
    }

    public fun toDbEntity(): MeetingDbEntity = MeetingDbEntity(
        id = 0,
        timestamp = timestamp,
        roomIdentifier = roomIdentifier,
        description = description
    )
    val formattedDateTime: String = "12.25"
        /*
        get() = if (isImmediate) "Прямо сейчас" else {
            //"${date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }*/
}

