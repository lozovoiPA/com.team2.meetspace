package com.team2.meetspace.data.repositories

import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.MeetingCreated
import com.team2.meetspace.data.entities.Result

class MeetingRepositoryImpl : MeetingRepository {
    override suspend fun createMeeting(meeting: Meeting): Result {
        val created = meeting.copy(id = "M-${System.currentTimeMillis()}")
        return MeetingCreated(created)
    }
}