package com.team2.meetspace.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2.meetspace.Dependencies
import com.team2.meetspace.NetworkConnectivityObserver
import com.team2.meetspace.data.PreferencesManager
import com.team2.meetspace.data.entities.ErrorResult
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.MeetingCreated
import com.team2.meetspace.data.entities.UserContact
import com.team2.meetspace.data.repositories.MeetingRepository
import com.team2.meetspace.data.repositories.UserContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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
    var showPermissionDialog: Boolean = false,
    var isConnected: StateFlow<Boolean>
)

enum class MeetingEditStep(var index: Int, var allowsPreviousStep: Boolean) {
    Creation(1, true),
    TimestampSelection(2, true),
    UserContactSelection(3, true),
    AskingUserContactsPermission(4, false),
    Error(5, false),
    Finished(6, false),
    SendForm(7, false)
}

class MeetingEditBottomSheetViewModel (
    private val meetingRepository: MeetingRepository,
    private val userContactRepository: UserContactRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {
    private val connectFlow = connectivityObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    private val _uiState = MutableStateFlow(MeetingEditBottomSheetState(
        isConnected = connectFlow
    ))
    val uiState = _uiState.asStateFlow()

    fun changeCheckedUserContact(contact: UserContact, checked: Boolean) {
        if (_uiState.value.contacts.isEmpty()) {
            val contacts = userContactRepository.retrieve()
            _uiState.update { it.copy(contacts = contacts) }
        }

        val currentSelected = _uiState.value.selectedContacts.toMutableList()
        val newSelected = if (checked) {
            if (!currentSelected.any { it.phone == contact.phone }) {
                currentSelected + contact
            } else currentSelected
        } else {
            currentSelected.filter { it.phone != contact.phone }
        }

        _uiState.update { it.copy(selectedContacts = newSelected) }
    }

    fun previousStep() {
        when (uiState.value.currentStep) {
            MeetingEditStep.TimestampSelection -> {
                changeStep(MeetingEditStep.Creation)
            }
            MeetingEditStep.UserContactSelection -> {
                if (_uiState.value.createNow) {
                    changeStep(MeetingEditStep.Creation)
                } else {
                    changeStep(MeetingEditStep.TimestampSelection)
                }
            }
            else -> {}
        }
    }

    fun nextStep() {
        when (uiState.value.currentStep) {
            MeetingEditStep.Creation -> {
                if (_uiState.value.createNow) {
                    changeStep(MeetingEditStep.AskingUserContactsPermission)
                } else {
                    changeStep(MeetingEditStep.TimestampSelection)
                }
            }
            MeetingEditStep.TimestampSelection -> {
                changeStep(MeetingEditStep.AskingUserContactsPermission)
            }
            MeetingEditStep.AskingUserContactsPermission -> {
                changeStep(MeetingEditStep.UserContactSelection)
            }
            MeetingEditStep.UserContactSelection -> {
                changeStep(MeetingEditStep.SendForm)
            }
            else -> {}
        }
    }

    fun setCreationTime(now: Boolean) {
        _uiState.update { it.copy(createNow = now) }
    }

    fun changeDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun changeDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun changeTime(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun createMeeting() {
        val timestamp = if (_uiState.value.createNow) {
            System.currentTimeMillis()
        } else {
            Dependencies.TimestampHelper().dateTimeToTimestamp(
                _uiState.value.selectedDate,
                _uiState.value.selectedTime
            )
        }

        viewModelScope.launch {
            when(val result = meetingRepository.create(timestamp, _uiState.value.description, _uiState.value.contacts)) {
                is MeetingCreated -> {
                    _uiState.update {
                        it.copy(
                            meeting = result.meeting,
                            meetingCreated = true
                        )
                    }
                    changeStep(MeetingEditStep.Finished)
                    Log.i("BottomSheet", "Meeting created in db")
                }
                is ErrorResult -> {
                    _uiState.update { it.copy(error = result.errorText) }
                    changeStep(MeetingEditStep.Error)
                }
                else -> {
                    _uiState.update { it.copy(error = "Неизвестная ошибка") }
                    changeStep(MeetingEditStep.Error)
                }
            }
        }
    }

    fun retrieveContacts() {
        Log.i("Info", "Зашли в retrieveContacts() вьюмодели");
        _uiState.update { it.copy(contacts = userContactRepository.retrieve()) }
        Log.i("Info", "Всего: ${_uiState.value.contacts.size} контактов получено");
    }

    fun skipContacts() {
        if(uiState.value.currentStep == MeetingEditStep.AskingUserContactsPermission) {
            changeStep(MeetingEditStep.SendForm)
        }
    }

    private fun changeStep(step: MeetingEditStep) {
        when (step) {
            MeetingEditStep.UserContactSelection -> { retrieveContacts() }
            else -> { }
        }
        _uiState.update { it.copy(currentStep = step) }
    }

    public fun clear() {
        _uiState.value = MeetingEditBottomSheetState(isConnected = connectFlow)
    }
}