package com.team2.meetspace.data.dataSources


import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.Result
import com.team2.meetspace.data.entities.MeetingDbEntity;
import com.team2.meetspace.data.entities.MeetingCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public class MeetingLocalDataSource(private val meetingDao: MeetingDao) {

    suspend fun create(meeting: Meeting): Result {
        try {
            val meetingDb = meeting.toDbEntity();
            withContext(Dispatchers.IO) {
                meetingDao.create(meetingDb);
            }
            val result = MeetingCreated(201, meetingDb);
            return result;
        } catch (e: Exception) {
            val errorExplain = "Error inserting meeting";
            println("${errorExplain}: ${e.message}");
            val result = ErrorResult(0, e.message ?: errorExplain);
            return result;
        }
    }

}
