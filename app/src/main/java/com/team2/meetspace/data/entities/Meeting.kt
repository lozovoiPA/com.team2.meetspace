package com.team2.meetspace.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Entity(tableName = "meetings")
data class MeetingDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp: Long,
    @ColumnInfo(name = "room_identifier") val roomIdentifier: String,
    val description: String
) { }

data class Meeting(
    val timestamp: Long,
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
    val formattedDateTime: String
        get() {
            return "${getDate()} ${getTime()}"
        }

    public fun getDate(): String {
        val instant = Instant.ofEpochMilli(timestamp);
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        val formatter = DateTimeFormatter.ofPattern("MM:dd");
        return formatter.format(localDate);
    }

    public fun getTime(): String {
        val instant = Instant.ofEpochMilli(timestamp);
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        val formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }
}

