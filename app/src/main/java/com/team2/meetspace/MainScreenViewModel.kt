package com.team2.meetspace

import androidx.lifecycle.ViewModel
import com.team2.meetspace.data.entities.Meeting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val upcomingMeetings: List<Meeting> = emptyList(),
    val showCreateBottomSheet: Boolean = false,
    val showMeetingInfo: Boolean = false,
    val createdMeeting: Meeting? = null
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
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
}