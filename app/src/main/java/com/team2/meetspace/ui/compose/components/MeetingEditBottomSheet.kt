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
import com.team2.meetspace.data.entities.Meeting
import com.team2.meetspace.data.entities.UserContact
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingCreateBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onMeetingCreated: (Meeting) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var currentStep by remember { mutableIntStateOf(1) }
    var isImmediate by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var selectedTime by remember { mutableStateOf(LocalTime.of(10, 35)) }

    var contacts by remember { mutableStateOf<List<UserContact>>(emptyList()) }
    val selectedContacts = remember { mutableStateListOf<UserContact>() }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            contacts = loadContacts(context)
        } else {
            showPermissionDialog = true
        }
    }

    LaunchedEffect(currentStep) {
        if (currentStep == 3 && contacts.isEmpty()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                contacts = loadContacts(context)
            } else {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Разрешить приложению Meetspace иметь доступ к контактам?") },
            text = { Text("Для выбора участников встречи необходим доступ к контактам") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }) {
                    Text("Разрешить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Запретить")
                }
            }
        )
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
                if (currentStep > 1) {
                    IconButton(onClick = { currentStep-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
                Text(
                    text = when (currentStep) {
                        1 -> "Создать встречу"
                        2 -> "Запланировать встречу"
                        3 -> "Контакты"
                        else -> "Создать встречу"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text("шаг $currentStep", style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            when (currentStep) {
                1 -> Step1TypeAndDescription(
                    isImmediate = isImmediate,
                    onImmediateChange = { isImmediate = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    onNext = { currentStep = 2 }
                )

                2 -> {
                    if (isImmediate) {
                        LaunchedEffect(Unit) {
                            currentStep = 3
                        }
                    } else {
                        Step2DateTime(
                            selectedDate = selectedDate,
                            selectedTime = selectedTime,
                            onDateChange = { selectedDate = it },
                            onTimeChange = { selectedTime = it },
                            onNext = { currentStep = 3 }
                        )
                    }
                }

                3 -> Step3Contacts(
                    contacts = contacts,
                    selectedContacts = selectedContacts,
                    onContactToggled = { contact, checked ->
                        if (checked) {
                            if (!selectedContacts.contains(contact)) {
                                selectedContacts.add(contact)
                            }
                        } else {
                            selectedContacts.removeAll { it.phone == contact.phone }
                        }
                    },
                    onSave = {
                        val meeting = Meeting(
                            timestamp = selectedTime.hour,
                            description = description.ifBlank { "Проверка проделанной работы" },
                            roomIdentifier = "10",
                            users = selectedContacts.toList()
                        )
                        onMeetingCreated(meeting)
                        scope.launch { sheetState.hide() }
                    }
                )
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