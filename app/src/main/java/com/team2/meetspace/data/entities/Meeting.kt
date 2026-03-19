package com.team2.meetspace.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.team2.meetspace.data.PreferencesManager
import java.time.Instant

import java.time.format.DateTimeFormatter

@Entity(tableName = "meetings")
data class MeetingDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timestamp: Long,
    @ColumnInfo(name = "room_identifier") val roomIdentifier: String,
    val description: String
) {
    @Ignore val userContacts: List<UserContactDbTuple> = emptyList()
}

data class Meeting(
    val timestamp: Long,
    val roomIdentifier: String,
    val description: String,
    val users: List<UserContact> = emptyList()
) {
    companion object {
        public val emptyMeeting: Meeting = Meeting(0, "", "");
        public fun fromDbEntity(meetingDb: MeetingDbEntity): Meeting {
            val users = mutableListOf<UserContact>();
            for (userContactDb in meetingDb.userContacts){
                users.add(UserContact.fromDbEntity(userContactDb))
            }
            val meeting = Meeting(meetingDb.timestamp, meetingDb.roomIdentifier, meetingDb.description, users)
            return meeting
        }
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
        val localDate = instant.atZone(PreferencesManager.systemTimeZone).toLocalDate();
        val formatter = DateTimeFormatter.ofPattern("dd.MM");
        return formatter.format(localDate);
    }

    public fun getTime(): String {
        val instant = Instant.ofEpochMilli(timestamp);
        val localTime = instant.atZone(PreferencesManager.systemTimeZone).toLocalTime();
        val formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }
}

