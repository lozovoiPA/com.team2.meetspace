package com.team2.meetspace.data.repositories

import com.team2.meetspace.data.dataSources.MeetingLocalDataSource
import com.team2.meetspace.data.dataSources.RoomRemoteDataSource
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.MeetingDbEntity
import com.team2.meetspace.data.entities.MeetingPlanned
import com.team2.meetspace.data.entities.Result

public class MeetingRepository(
    private val roomsRemote: RoomRemoteDataSource,
    private val meetingsLocal: MeetingLocalDataSource
) {
    suspend fun create(
        timestamp: Long,
        description: String) : Result
    {
        var result = roomsRemote.create(timestamp);
        if (result.code != 200){
            return result;
        }
        lateinit var identifier: String;
        when (result) {
            is MeetingPlanned -> identifier = result.identifier;
            else -> return result;
        }
        val newMeeting = Meeting(
            timestamp,
            identifier,
            description
        );

        result = meetingsLocal.create(newMeeting);
        return result;
    }
}