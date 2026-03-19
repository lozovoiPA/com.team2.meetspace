package com.team2.meetspace.ui.compose.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.UserContact
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
import com.team2.meetspace.ui.viewModel.MeetingEditStep
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingCreateBottomSheet(
    meeting: Meeting = Meeting.emptyMeeting,
    viewModel: MeetingEditBottomSheetViewModel = viewModel(),
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onMeetingCreated: (Meeting) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.nextStep()
        } else {
            viewModel.skipContacts()
        }
    }

    if (state.currentStep == MeetingEditStep.AskingUserContactsPermission) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            PermissionCard(
                icon = Icons.Outlined.AccountCircle,
                title = "Разрешить приложению Meetspace иметь доступ к контактам?",
                description = "Для выбора участников встречи необходим доступ к контактам",
                allowButtonText = "Разрешить",
                denyButtonText = "Запретить",
                onAllowClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                onDenyClick = { viewModel.skipContacts() }
            )
        } else {
            LaunchedEffect(Unit) {
                viewModel.nextStep()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                if (state.currentStep.allowsPreviousStep) {
                    IconButton(onClick = { viewModel.previousStep() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
                Text(
                    text = when (state.currentStep) {
                        MeetingEditStep.Creation -> "Создать\nвстречу"
                        MeetingEditStep.TimestampSelection -> "Запланировать\nвстречу"
                        MeetingEditStep.UserContactSelection -> "Контакты"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text("шаг ${state.currentStep.index}", style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            when (state.currentStep) {
                MeetingEditStep.Creation ->
                    Step1TypeAndDescription(
                        isImmediate = state.createNow,
                        onImmediateChange = { viewModel.setCreationTime(it) },
                        description = state.description,
                        onDescriptionChange = { viewModel.changeDescription(it) },
                        onNext = { viewModel.nextStep() }
                    )

                MeetingEditStep.TimestampSelection ->
                    Step2DateTime(
                        selectedDate = state.selectedDate,
                        selectedTime = state.selectedTime,
                        onDateChange = { viewModel.changeDate(it) },
                        onTimeChange = { viewModel.changeTime(it) },
                        onNext = { viewModel.nextStep() }
                    )

                MeetingEditStep.UserContactSelection ->
                    Step3Contacts(
                        contacts = state.contacts,
                        selectedContacts = state.selectedContacts,
                        onContactToggled = { contact, checked ->
                            viewModel.changeCheckedUserContact(contact, checked)
                        },
                        onSave = {
                            viewModel.createMeeting()
                            scope.launch { sheetState.hide() }
                        }
                    )

                MeetingEditStep.SendForm -> {
                    viewModel.createMeeting()
                }

                MeetingEditStep.Finished -> {
                    LaunchedEffect(state.meeting) {
                        onMeetingCreated(state.meeting)
                        viewModel.clear()
                    }
                }

                else -> {}
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}