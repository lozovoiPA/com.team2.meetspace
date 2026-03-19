package com.team2.meetspace.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "meeting_userContacts",
    foreignKeys = [
        ForeignKey(
            entity = MeetingDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["meeting_id"]
        )
    ]
)
data class UserContactDbTuple(
    @PrimaryKey(true) val id: Int,
    @ColumnInfo(name = "meeting_id") val meetingId: Int,
    val phone: String,
    val name: String
)