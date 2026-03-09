package com.team2.meetspace

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.team2.meetspace.data.dataSources.MeetingLocalDataSource
import com.team2.meetspace.data.dataSources.MeetspaceAppDb
import com.team2.meetspace.data.dataSources.RoomRemoteDataSource
import com.team2.meetspace.data.repositories.MeetingRepository
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel

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
    val meetingRepository: MeetingRepository by lazy {
        MeetingRepository(roomRemoteDataSource, meetingLocalDataSource);
    }
}

class MeetingEditBottomSheetViewModelFactory(var dependencies: Dependencies) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MeetingEditBottomSheetViewModel(dependencies.meetingRepository) as T
    }
}

