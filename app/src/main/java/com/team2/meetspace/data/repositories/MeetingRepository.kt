package com.team2.meetspace.data.repositories

import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.Result

interface MeetingRepository {
    suspend fun createMeeting(meeting: Meeting): Result
}