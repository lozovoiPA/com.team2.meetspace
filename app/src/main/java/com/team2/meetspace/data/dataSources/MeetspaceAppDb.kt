package com.team2.meetspace.data.dataSources

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.RoomDatabase
import com.team2.meetspace.data.entities.Meeting

@Database(
    version = 1,
    entities = [
        Meeting::class
    ]
)
abstract class MeetspaceAppDb : RoomDatabase(){
    abstract fun getMeetingDao(): MeetingDao
}

@Dao
interface MeetingDao {
    @Insert(entity = Meeting::class)
    fun create(meeting: Meeting)
}