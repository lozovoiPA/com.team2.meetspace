package com.team2.meetspace.data.dataSources

import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import com.team2.meetspace.data.entities.MeetingDbEntity
import com.team2.meetspace.data.entities.UserContactDbTuple

@Database(
    version = 1,
    entities = [
        MeetingDbEntity::class,
        UserContactDbTuple::class
    ]
)
abstract class MeetspaceAppDb : RoomDatabase(){
    abstract fun getMeetingDao(): MeetingDao
}

