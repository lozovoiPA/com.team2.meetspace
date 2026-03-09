package com.team2.meetspace.ui.compose.components

import androidx.compose.runtime.snapshots.SnapshotStateList
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.UserContact
import com.team2.meetspace.ui.viewModel.MeetingEditBottomSheetViewModel
import com.team2.meetspace.ui.viewModel.MeetingEditStep
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingCreateBottomSheet(
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
            //state.contacts = loadContacts(context)
        } else {
            state.showPermissionDialog = true
        }
    }

    if (state.currentStep == MeetingEditStep.AskingUserContactsPermission) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog(
                onDismissRequest = { viewModel.skipContacts(); },
                title = { Text("Разрешить приложению Meetspace иметь доступ к контактам?") },
                text = { Text("Для выбора участников встречи необходим доступ к контактам") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.nextStep();
                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }) {
                        Text("Разрешить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.skipContacts(); }) {
                        Text("Запретить")
                    }
                }
            )
        }
        else {
            viewModel.nextStep();
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
                    IconButton(onClick = { viewModel.previousStep(); }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
                Text(
                    text = when (state.currentStep) {
                        MeetingEditStep.Creation -> "Создать встречу"
                        MeetingEditStep.TimestampSelection -> "Запланировать встречу"
                        MeetingEditStep.UserContactsSelection -> "Контакты"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text("шаг ${state.currentStep.index}", style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            when(state.currentStep) {
                MeetingEditStep.Creation ->
                    Step1TypeAndDescription(
                        isImmediate = state.createNow,
                        onImmediateChange = { now -> viewModel.setCreationTime(now); },
                        description = state.description,
                        onDescriptionChange = { description -> viewModel.changeDescription(description); },
                        onNext = { viewModel.nextStep(); }
                    );
                MeetingEditStep.TimestampSelection ->
                    Step2DateTime(
                        selectedDate = state.selectedDate,
                        selectedTime = state.selectedTime,
                        onDateChange = { date -> viewModel.changeDate(date); },
                        onTimeChange = { time -> viewModel.changeTime(time); },
                        onNext = { viewModel.nextStep(); }
                    );
                MeetingEditStep.UserContactsSelection ->
                    Step3Contacts(
                        contacts = state.contacts,
                        selectedContacts = state.selectedContacts,
                        onContactToggled = { contact, checked ->
                            if (checked) {
                                if (!state.selectedContacts.contains(contact)) {
                                    //state.selectedContacts.add(contact)
                                }
                            } else {
                                //state.selectedContacts.removeAll { it.phone == contact.phone }
                            }
                        },
                        onSave = {
                            viewModel.nextStep();
                            scope.launch { sheetState.hide() }
                        }
                    );
                MeetingEditStep.SendForm -> { viewModel.createMeeting(); }
                MeetingEditStep.Finished -> { onMeetingCreated(state.meeting); }
                else -> {};
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun loadContacts(context: Context): List<UserContact> {
    val list = mutableListOf<UserContact>()
    context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        ),
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while (cursor.moveToNext()) {
            val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
            val phone = if (numberIndex >= 0) cursor.getString(numberIndex) else null
            if (!phone.isNullOrBlank()) {
                list.add(UserContact(name, phone))
            }
        }
    }
    return list.distinctBy { it.phone }
}