package com.team2.meetspace.ui.viewModel

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.team2.meetspace.data.entities.Meeting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
data class MainUiState(
    val upcomingMeetings: List<Meeting> = emptyList(),
    val showCreateBottomSheet: Boolean = false,
    val showMeetingInfo: Boolean = false,
    val createdMeeting: Meeting? = null,
    val isConnected: Boolean = true,
    val isLoading: Boolean = false
)


object NetworkUtil{
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean{
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {return true}
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun isInternetAvailable(): Boolean {
        return  withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress( "8.8.8.8", 53), 1500)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        }
    }

}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(    MainUiState())
    val uiState = _uiState.asStateFlow()
    fun checkInternet() {
        _uiState.update { it.copy(isLoading = true) }

        // Используем viewModelScope вместо runBlocking
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val hasNetwork = try {
                NetworkUtil.isNetworkAvailable(context)
            } catch (e: SecurityException) {
                false
            }

            val hasInternet = NetworkUtil.isInternetAvailable()

            _uiState.update {
                it.copy(
                    isConnected = hasInternet,
                    isLoading = false
                )
            }
        }
    }

    init{
        viewModelScope.launch {
            checkInternet()
        }
    }

    fun showCreateSheet() {
        _uiState.update { it.copy(showCreateBottomSheet = true) }
    }

    fun hideCreateSheet() {
        _uiState.update { it.copy(showCreateBottomSheet = false) }
    }

    fun addMeeting(meeting: Meeting) {
        _uiState.update { it.copy(upcomingMeetings = it.upcomingMeetings + meeting) }
    }

    fun showMeetingInfo(meeting: Meeting) {
        _uiState.update {
            it.copy(
                showMeetingInfo = true,
                createdMeeting = meeting
            )
        }
    }

    fun hideMeetingInfo() {
        _uiState.update { it.copy(showMeetingInfo = false, createdMeeting = null) }
    }
}