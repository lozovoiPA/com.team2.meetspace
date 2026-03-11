package com.team2.meetspace.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2.meetspace.Dependencies
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.ServerError
import com.team2.meetspace.data.entities.MeetingCreated
import com.team2.meetspace.data.entities.Result
import com.team2.meetspace.data.entities.UserContact
import com.team2.meetspace.data.repositories.MeetingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

data class MeetingEditBottomSheetState(
    var meeting: Meeting = Meeting.emptyMeeting,
    var meetingCreated: Boolean = false,
    var error: String? = null,

    var currentStep: MeetingEditStep = MeetingEditStep.Creation,
    var createNow: Boolean = true,
    var description: String = "",
    var selectedDate: LocalDate = LocalDate.now(PreferencesManager.systemTimeZone).plusDays(1),
    var selectedTime: LocalTime = LocalTime.now(),

    var contacts: List<UserContact> = emptyList(),
    var selectedContacts: List<UserContact> = emptyList(),

    var showPermissionDialog: Boolean = false
) {}

enum class MeetingEditStep(var index: Int, var allowsPreviousStep: Boolean) {
    Creation(1, true),
    TimestampSelection(2, true),
    UserContactsSelection(3, true),
    AskingUserContactsPermission(4, false),
    Error(5, false),
    Finished(6, false),
    SendForm(7, false)
}

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

    fun previousStep() {
        if(uiState.value.currentStep == MeetingEditStep.TimestampSelection){
            _uiState.update { it.copy(currentStep = MeetingEditStep.Creation); };
        } else if (uiState.value.currentStep == MeetingEditStep.UserContactsSelection) {
            _uiState.update { it.copy(currentStep = MeetingEditStep.TimestampSelection); };
        }
    }

    fun nextStep() {
        when (uiState.value.currentStep) {
            MeetingEditStep.Creation -> {
                changeStep(MeetingEditStep.TimestampSelection);
            }
            MeetingEditStep.TimestampSelection -> {
                changeStep(MeetingEditStep.AskingUserContactsPermission);
            }
            MeetingEditStep.AskingUserContactsPermission -> {
                changeStep(MeetingEditStep.UserContactsSelection);
            }
            MeetingEditStep.UserContactsSelection -> {
                changeStep(MeetingEditStep.SendForm);
            }
            else -> {};
        }

    }



    fun setCreationTime(now: Boolean){
        _uiState.update { it.copy(createNow = now); };
    }

    fun changeDescription(description: String){
        _uiState.update { it.copy(description = description); };
    }

    fun changeDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date); };
    }

    fun changeTime(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time); };
    }

    fun createMeeting() {
        val timestamp = Dependencies.TimestampHelper().dateTimeToTimestamp(_uiState.value.selectedDate, _uiState.value.selectedTime);

        viewModelScope.launch {
            when(val result = meetingRepository.create(timestamp, _uiState.value.description)) {
                is MeetingCreated -> {
                    _uiState.update { it.copy(meeting = result.meeting); };
                    changeStep(MeetingEditStep.Finished);

                    Log.e("GOOD", "Meeting created: ${result.meeting.timestamp} ${result.meeting.description}");
                };
                is ErrorResult -> {
                    _uiState.update { it.copy(error = result.errorText); };
                    changeStep(MeetingEditStep.Error);

                    Log.e("BAD", "Meeting failed! ${result.errorText}");
                };
                else -> {
                    _uiState.update { it.copy(error = "Неизвестная ошибка"); };
                    changeStep(MeetingEditStep.Error);

                    Log.e("BAD", "Meeting failed! Unknown error");
                }
            }
        }
    }

    fun skipContacts() {
        if(_uiState.value.currentStep == MeetingEditStep.AskingUserContactsPermission){
            changeStep(MeetingEditStep.SendForm);
        }
    }

    private fun changeStep(step: MeetingEditStep) {
        _uiState.update { it.copy(currentStep = step); };
    }
}