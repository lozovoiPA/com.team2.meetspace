package com.team2.meetspace.data.dataSources

import android.net.ConnectivityManager
import com.team2.meetspace.Dependencies
import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.MeetingPlanned
import com.team2.meetspace.data.entities.Result;
import java.util.UUID

public class RoomRemoteDataSource(var connectivityManager: ConnectivityManager) {

    // Создание комнаты на время
    public fun create(timestamp: Int): Result {
        if(Dependencies.NetworkHelper().checkConnection(connectivityManager)){
            return sendRoomCreateRequest(timestamp);
        }
        return ErrorResult(0, "No internet connection");
    }

    private fun sendRoomCreateRequest(timestamp: Int): Result {
        return MeetingPlanned(200, UUID.randomUUID().toString());
    }
}


