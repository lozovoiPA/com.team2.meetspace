package com.team2.meetspace

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.data.dataSources.MeetingLocalDataSource
import com.team2.meetspace.data.dataSources.MeetspaceAppDb
import com.team2.meetspace.data.dataSources.RoomRemoteDataSource
import com.team2.meetspace.data.dataSources.UserContactLocalDataSource
import com.team2.meetspace.data.repositories.MeetingRepository
import com.team2.meetspace.data.repositories.UserContactRepository
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
import com.team2.meetspace.ui.viewModel.MainScreenViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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

    public class TimestampHelper {
        public fun dateTimeToTimestamp(date: LocalDate, time: LocalTime): Long {
            val dateTime = date.atTime(time);
            return dateTimeToTimestamp(dateTime);
        }

        public fun dateTimeToTimestamp(dateTime: LocalDateTime): Long {
            val zonedDateTime = dateTime.atZone(PreferencesManager.systemTimeZone);
            val timestamp = zonedDateTime.toInstant().toEpochMilli();
            return timestamp;
        }
    }

    val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val networkConnectivityObserver: NetworkConnectivityObserver by lazy {
        NetworkConnectivityObserver(context)
    }
    val meetingLocalDataSource: MeetingLocalDataSource by lazy {
        MeetingLocalDataSource(meetspaceAppDb!!.getMeetingDao());
    }
    val roomRemoteDataSource: RoomRemoteDataSource by lazy {
        RoomRemoteDataSource(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager);
    }
    val userContactLocalDataSource: UserContactLocalDataSource by lazy {
        UserContactLocalDataSource(context.contentResolver);
    }
    val userContactRepository: UserContactRepository by lazy {
        UserContactRepository(userContactLocalDataSource);
    }
    val meetingRepository: MeetingRepository by lazy {
        MeetingRepository(roomRemoteDataSource, meetingLocalDataSource);
    }
}

class MeetingEditBottomSheetViewModelFactory(var dependencies: Dependencies) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MeetingEditBottomSheetViewModel(
            dependencies.meetingRepository,
            dependencies.userContactRepository,
            dependencies.networkConnectivityObserver
        ) as T
    }
}

class MainScreenViewModelFactory(var dependencies: Dependencies) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainScreenViewModel(
            dependencies.meetingRepository,
            dependencies.networkConnectivityObserver
        ) as T
    }
}

class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}