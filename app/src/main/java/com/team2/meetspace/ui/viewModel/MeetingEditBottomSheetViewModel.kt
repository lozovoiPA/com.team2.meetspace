package com.team2.meetspace.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.ServerError
import com.team2.meetspace.data.entities.MeetingCreated
import com.team2.meetspace.data.repositories.MeetingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MeetingEditBottomSheetState(
    val meeting: Meeting = Meeting.emptyMeeting,
    val meetingCreated: Boolean = false,
    val error: String? = null
)

class MeetingEditBottomSheetViewModel(
    private val meetingRepository: MeetingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeetingEditBottomSheetState())
    val uiState = _uiState.asStateFlow()

    fun createMeeting(meeting: Meeting) {
        viewModelScope.launch {
            val result = meetingRepository.create(meeting.timestamp, meeting.description)
            when (result) {
                is MeetingCreated -> {
                    _uiState.update {
                        it.copy(
                            meetingCreated = true,
                            meeting = result.meeting,
                            error = null
                        )
                    }
                }
                is ServerError -> {
                    _uiState.update { it.copy(error = result.errorText) }
                }
                else -> {
                    _uiState.update { it.copy(error = "Неизвестная ошибка") }
                }
            }
        }
    }
}