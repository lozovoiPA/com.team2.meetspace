package com.team2.meetspace.data.repositories

import com.team2.meetspace.data.dataSources.MeetingLocalDataSource
import com.team2.meetspace.data.dataSources.RoomRemoteDataSource
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.MeetingDbEntity
import com.team2.meetspace.data.entities.MeetingPlanned
import com.team2.meetspace.data.entities.UserContact
import com.team2.meetspace.data.entities.Result

public class MeetingRepository(
    private val roomsRemote: RoomRemoteDataSource,
    private val meetingsLocal: MeetingLocalDataSource
) {
    suspend fun create(
        timestamp: Long,
        description: String,
        userContacts: List<UserContact>
    ) : Result
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
            description,
            userContacts
        );

        result = meetingsLocal.create(newMeeting);
        return result;
    }

    suspend fun retrieve() : Result {
        val result = meetingsLocal.retrieve()
        return result
    }
}