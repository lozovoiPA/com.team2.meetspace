package com.team2.meetspace

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.team2.meetspace.data.dataSources.MeetingLocalDataSource
import com.team2.meetspace.data.dataSources.MeetspaceAppDb
import com.team2.meetspace.data.dataSources.RoomRemoteDataSource

class Dependencies(var context: Context) {
    init {
        if(meetspaceAppDb == null){
            meetspaceAppDb = Room.databaseBuilder(
                context,
                MeetspaceAppDb::class.java,
                "database.db"
            ).build();
        }
    }
    companion object {
        private var meetspaceAppDb: MeetspaceAppDb? = null;
    }
    public class NetworkHelper {

        public fun checkConnection(connectivityManager: ConnectivityManager): Boolean {
            val network = connectivityManager.activeNetwork ?: return false;
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }

    }

    // Т.к. доступ из объекта, который инициализируется по контексту, то Db != null
    val meetingLocalDataSource: MeetingLocalDataSource by lazy {
        MeetingLocalDataSource(meetspaceAppDb!!.getMeetingDao());
    }
    val roomRemoteDataSource: RoomRemoteDataSource by lazy {
        RoomRemoteDataSource(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager);
    }

}

