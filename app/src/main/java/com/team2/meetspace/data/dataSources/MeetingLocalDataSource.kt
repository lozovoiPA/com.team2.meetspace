package com.team2.meetspace.data.dataSources


import android.util.Log
import androidx.room.Query
import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.Result
import com.team2.meetspace.data.entities.MeetingDbEntity;
import com.team2.meetspace.data.entities.MeetingCreated
import com.team2.meetspace.data.entities.MeetingsRetrieved
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public class MeetingLocalDataSource(private val meetingDao: MeetingDao) {

    suspend fun create(meeting: Meeting): Result {
        try {
            val meetingDb = meeting.toDbEntity();
            withContext(Dispatchers.IO) {
                meetingDao.create(meetingDb);
            }
            val result = MeetingCreated(201, meeting);
            return result;
        } catch (e: Exception) {
            val errorExplain = "Ошибка - создать встречу не удалось";
            Log.e("Database", "${errorExplain}: ${e.message}");
            val result = ErrorResult(0, e.message ?: errorExplain);
            return result;
        }
    }

    suspend fun retrieve(): Result {
        try {
            var meetingsDb = emptyList<MeetingDbEntity>()
            withContext(Dispatchers.IO) {
                meetingsDb = meetingDao.retrieve()
            }
            val meetings = mutableListOf<Meeting>()
            for (meetingDb in meetingsDb) {
                meetings.add(Meeting.fromDbEntity(meetingDb))
            }
            val result = MeetingsRetrieved(200, meetings)
            Log.i("MeetingsLocalDataSource", "${result.meetings.size} found")
            return result
        } catch (e: Exception) {
            val errorExplain = "Ошибка - получить встречи не удалось";
            Log.e("Database","${errorExplain}: ${e.message}");
            val result = ErrorResult(0, e.message ?: errorExplain);
            return result;
        }
    }
}
