package com.team2.meetspace.data.dataSources

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.team2.meetspace.data.entities.MeetingDbEntity

@Database(
    version = 1,
    entities = [
        MeetingDbEntity::class
    ]
)
abstract class MeetspaceAppDb : RoomDatabase(){
    abstract fun getMeetingDao(): MeetingDao
}

@Dao
interface MeetingDao {
    @Insert(entity = MeetingDbEntity::class)
    fun create(meeting: MeetingDbEntity)
}