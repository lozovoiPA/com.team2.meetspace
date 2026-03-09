package com.team2.meetspace.ui.components

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
import java.util.UUID

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
                            id = UUID.randomUUID().toString().take(8).uppercase(),
                            description = description.ifBlank { "Проверка проделанной работы" },
                            date = selectedDate,
                            time = selectedTime,
                            users = selectedContacts.toList(),
                            isImmediate = isImmediate
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

@Composable
private fun Step1TypeAndDescription(
    isImmediate: Boolean,
    onImmediateChange: (Boolean) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(
            "Тип встречи",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = isImmediate,
                    onValueChange = { onImmediateChange(true) },
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isImmediate, onClick = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Создать прямо сейчас",
                    fontWeight = if (isImmediate) FontWeight.Bold else FontWeight.Normal)
                Text("Встреча начнется сразу после создания",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = !isImmediate,
                    onValueChange = { onImmediateChange(false) },
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = !isImmediate, onClick = null)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Запланировать на время",
                    fontWeight = if (!isImmediate) FontWeight.Bold else FontWeight.Normal)
                Text("Выберите дату и время встречи",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Тема встречи (необязательно)") },
            placeholder = { Text("Проверка проделанной работы") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Далее")
        }
    }
}

@Composable
private fun Step2DateTime(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(
            "Выберите дату и время",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DateTimePicker(
            initialDate = selectedDate,
            initialTime = selectedTime,
            onDateSelected = onDateChange,
            onTimeSelected = onTimeChange
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Выбрать участников")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now().plusDays(1),
    initialTime: LocalTime = LocalTime.of(10, 0),
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: initialDate

    val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate)
    }

    LaunchedEffect(selectedTime) {
        onTimeSelected(selectedTime)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            onValueChange = { },
            readOnly = true,
            label = { Text("Дата") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Выбрать дату"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            onValueChange = { },
            readOnly = true,
            label = { Text("Время") },
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Выбрать время"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Отмена")
                }
            },
            title = { Text("Выберите дату") },
            text = {
                DatePicker(state = datePickerState)
            }
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false }
                ) {
                    Text("Отмена")
                }
            },
            title = { Text("Выберите время") },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
private fun Step3Contacts(
    contacts: List<UserContact>,
    selectedContacts: SnapshotStateList<UserContact>,
    onContactToggled: (UserContact, Boolean) -> Unit,
    onSave: () -> Unit
) {
    Column {
        Text(
            "Выберите участников",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Нет контактов для отображения",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(contacts) { contact ->
                    ContactItem(
                        contact = contact,
                        isSelected = selectedContacts.contains(contact),
                        onToggle = { checked -> onContactToggled(contact, checked) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Создать встречу")
        }
    }
}

@Composable
private fun ContactItem(
    contact: UserContact,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = isSelected,
                onValueChange = onToggle,
                role = Role.Checkbox
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = null
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = contact.displayName,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (contact.phone != null) {
                Text(
                    text = contact.phone,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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