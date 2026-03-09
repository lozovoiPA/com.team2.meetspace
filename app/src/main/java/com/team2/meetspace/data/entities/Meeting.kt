package com.team2.meetspace.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val description: String
) {
    public fun toDbEntity(): MeetingDbEntity = MeetingDbEntity(
        id = 0,
        timestamp = timestamp,
        roomIdentifier = roomIdentifier,
        description = description
    )
}

