package com.team2.meetspace.data.dataSources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.team2.meetspace.data.entities.MeetingDbEntity

@Dao
interface MeetingDao {
    @Insert(entity = MeetingDbEntity::class)
    fun create(meeting: MeetingDbEntity)

    @Query(
        "SELECT meetings.id, timestamp, room_identifier, description FROM meetings\n" +
                "LEFT JOIN meeting_userContacts ON meetings.id = meeting_userContacts.meeting_id\n"
    )
    fun retrieve(): List<MeetingDbEntity>
}
