package com.team2.meetspace.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.team2.meetspace.Dependencies
import com.team2.meetspace.data.PreferencesManager
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.now().plusDays(1),
    initialTime: LocalTime = LocalTime.of(10, 0),
    onDateSelected: (LocalDate, LocalTime) -> Unit,
    onTimeSelected: (LocalDate, LocalTime) -> Unit,
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Dependencies.TimestampHelper().dateTimeToTimestamp(initialDate, initialTime)
    )

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
        Instant.ofEpochMilli(millis).atZone(PreferencesManager.systemTimeZone).toLocalDate()
    } ?: initialDate

    val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate, selectedTime)
    }

    LaunchedEffect(selectedTime) {
        onTimeSelected(selectedDate, selectedTime)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            onValueChange = { },
            readOnly = true,
            label = { Text("Дата") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker.value = true }) {
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
                IconButton(onClick = { showTimePicker.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Выбрать время"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    val closeDatePicker = {
        showDatePicker.value = false
    }
    val closeTimePicker = {
        showTimePicker.value = false
    }

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = closeDatePicker,
            confirmButton = {
                TextButton(onClick = closeDatePicker) { Text("OK") }
            },
            modifier = Modifier.scale(0.9f)
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                modifier = Modifier.scale(0.9f)
            )
        }
    }

    if (showTimePicker.value) {
        AlertDialog(
            onDismissRequest = closeTimePicker,
            confirmButton = {
                TextButton(
                    onClick = closeTimePicker
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = closeTimePicker
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
