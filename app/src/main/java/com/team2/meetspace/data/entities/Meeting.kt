package com.team2.meetspace.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meetings")
data class Meeting(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "room_identifier") var roomIdentifier: String,
    var description: String
) { }

