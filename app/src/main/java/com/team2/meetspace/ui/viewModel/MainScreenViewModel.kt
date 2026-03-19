package com.team2.meetspace.ui.viewModel

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import com.team2.meetspace.data.entities.Meeting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.team2.meetspace.Dependencies
import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.MeetingsRetrieved
import com.team2.meetspace.data.repositories.MeetingRepository
import kotlinx.coroutines.launch
data class MainUiState(
    val upcomingMeetings: List<Meeting> = emptyList(),
    val showCreateBottomSheet: Boolean = false,
    val showMeetingInfo: Boolean = false,
    val createdMeeting: Meeting? = null,
    val isConnected: Boolean = true,
    val isLoading: Boolean = false,
    val errorText: String = ""
)

class MainScreenViewModel(
    private val meetingRepository: MeetingRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(    MainUiState())
    val uiState = _uiState.asStateFlow()
    fun checkInternet() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val hasInternet = Dependencies.NetworkHelper().checkConnection(connectivityManager)
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

    fun retrieveMeetings() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when(val result = meetingRepository.retrieve()) {
                is MeetingsRetrieved -> _uiState.update { it.copy(upcomingMeetings = result.meetings) }
                is ErrorResult -> _uiState.update { it.copy(errorText = result.errorText) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}