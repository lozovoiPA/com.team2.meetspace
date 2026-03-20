package com.team2.meetspace.ui.viewModel

import androidx.lifecycle.ViewModel
import com.team2.meetspace.data.entities.Meeting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.team2.meetspace.NetworkConnectivityObserver
import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.MeetingsRetrieved
import com.team2.meetspace.data.repositories.MeetingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
data class MainUiState(
    val upcomingMeetings: List<Meeting> = emptyList(),
    val showCreateBottomSheet: Boolean = false,
    val showMeetingInfo: Boolean = false,
    val createdMeeting: Meeting? = null,
    val isConnected: StateFlow<Boolean>,
    val isLoading: Boolean = false,
    val errorText: String = "",
    val displayConnectionError: Boolean = false
) {
}

class MainScreenViewModel(
    private val meetingRepository: MeetingRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {
    private val _uiState = MutableStateFlow(    MainUiState(
        isConnected = connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            ))
    )
    val uiState = _uiState.asStateFlow()

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

    fun showError() {
        _uiState.update { it.copy(displayConnectionError = true) }
    }

    fun hideError() {
        _uiState.update { it.copy(displayConnectionError = false) }
    }
}